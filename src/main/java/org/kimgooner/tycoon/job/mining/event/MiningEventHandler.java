package org.kimgooner.tycoon.job.mining.event;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.job.mining.controller.MiningController;
import org.kimgooner.tycoon.job.mining.dto.MiningDataRequestDto;
import org.kimgooner.tycoon.job.mining.dto.MiningResultDto;
import org.kimgooner.tycoon.job.mining.model.MiningStat;
import org.kimgooner.tycoon.job.mining.service.*;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class MiningEventHandler implements Listener {
    private final Set<UUID> editingMode;
    private final Set<UUID> statMode;
    private final Map<UUID, MiningStat> miningStatMap;

    private final MiningStatService miningStatService;
    private final MiningBuffService miningBuffService;
    private final MiningDropService miningDropService;
    private final MiningDataService miningDataService;
    private final MiningAttributeService miningAttributeService;
    private final BlockRegenService blockRegenService;
    private final BlockSpreadService blockSpreadService;

    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    private final Map<String, Integer> REGION_MAP = Map.of( // region 테이블
            "mine1", 1,
            "mine2", 2,
            "mine3", 3,
            "mine4", 4
    );

    public MiningEventHandler(JavaPlugin plugin, MiningController miningController,
                              Set<UUID> editingMode,
                              MiningStatService miningStatService,
                              MiningBuffService miningBuffService,
                              MiningDropService miningDropService,
                              MiningDataService miningDataService,
                              MiningAttributeService miningAttributeService,
                              BlockRegenService blockRegenService,
                              BlockSpreadService blockSpreadService
                              ) {
        this.editingMode = editingMode;
        this.statMode = miningController.getStatMode();
        this.miningStatMap = miningController.getMiningMap();

        this.miningStatService = miningStatService;
        this.miningBuffService = miningBuffService;
        this.miningDropService = miningDropService;
        this.miningDataService = miningDataService;
        this.miningAttributeService = miningAttributeService;
        this.blockRegenService = blockRegenService;
        this.blockSpreadService = blockSpreadService;
    }

    private Integer isInMine(Location loc) { // 공간 여부
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
        ApplicableRegionSet regionSet = Objects.requireNonNull(regions).getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            return REGION_MAP.get(region.getId());
        }
        return 0;
    }

    @EventHandler
    public void onBlockDamageCalc(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        int floor = isInMine(block.getLocation());

        if (!miningDropService.oreDropTable.containsKey(material)) {
            event.setCancelled(true);  // 다른 블럭은 부수기 못 하게 막음
            return;
        }

        MiningDataRequestDto dto = miningDataService.getPlayerData(player);
        MiningStat miningOverall = miningStatService.getCachedStat(player, floor, dto);
        miningStatMap.put(player.getUniqueId(), miningOverall);

        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        miningAttributeService.calcMiningSpeed(miningOverall, player);

        double currentValue = attr.getValue(); // 최종 값 (base + modifiers 반영)
        double baseValue = attr.getBaseValue(); // 기본값 (setBaseValue로 지정한 값)

        player.sendMessage("현재 채굴 속도 값: " + currentValue);
        player.sendMessage("기본 채굴 속도 값: " + baseValue);
    }

    // 광물 드랍 시스템
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        int floor = isInMine(block.getLocation());

        if(floor == 0){return;} // 광산이 아닐 경우.
        if(!miningDropService.oreDropTable.containsKey(material)){return;} // 드랍 테이블에 없는 항목인 경우
        if(editingMode.contains(player.getUniqueId())) {return;} // 에딧 모드일 경우
        event.setDropItems(false);

        MiningStat miningStat = miningStatMap.get(player.getUniqueId()); // miningMap에서 스탯 가져오기

        if(statMode.contains(player.getUniqueId())) {
            miningStat.printStat(player);
        }

        MiningResultDto dto = miningDropService.getDropItem(player, miningStat, material, floor);

        miningDataService.processMiningData(player, miningStat, dto, floor); // 데이터 처리 ( 경험치, 아이템, 가루 )
        miningBuffService.processBuff(player, miningStat); // 스택형 버프 처리.
        blockRegenService.getRegenBlock(player, miningStat, block, floor);
        blockSpreadService.applyBlockSpread(player, miningStat, block, floor);
    }
}
