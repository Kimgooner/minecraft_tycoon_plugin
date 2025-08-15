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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.job.mining.controller.MiningController;
import org.kimgooner.tycoon.job.mining.model.MiningStat;
import org.kimgooner.tycoon.job.mining.service.*;

import java.util.*;


public class MiningEventHandler implements Listener {
    private final JavaPlugin plugin;
    private final Set<UUID> editingMode;
    private final Set<UUID> statMode;
    private final Map<UUID, MiningStat> miningStatMap;

    private final MiningStatService miningStatService;
    private final MiningBuffService miningBuffService;
    private final MiningDropService miningDropService;
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
                              BlockRegenService blockRegenService,
                              BlockSpreadService blockSpreadService
                              ) {
        this.plugin = plugin;
        this.editingMode = editingMode;
        this.statMode = miningController.getStatMode();
        this.miningStatMap = miningController.getMiningMap();

        this.miningStatService = miningStatService;
        this.miningBuffService = miningBuffService;
        this.miningDropService = miningDropService;
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

        MiningStat miningOverall = miningStatService.getCachedStat(player, floor);
        miningStatMap.put(player.getUniqueId(), miningOverall);

        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

//        miningUtil.removeMiningSpeedModifier(player);
//        miningUtil.applyMiningSpeedStat(player, miningOverall.getSpeed());
    }

    private final List<Integer> DUST_BASE = List.of(
            2, 4, 6, 10
    );

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

        MiningStat miningStat = miningStatMap.get(player.getUniqueId());
        MiningDropService.dropResultData dropResultData = miningDropService.getDropItem(player, miningStat, material, floor);

        if(statMode.contains(player.getUniqueId())) {
            miningStat.printStat(player);
        }

        int exp = dropResultData.exp();
        int grade = dropResultData.grade();
        miningStatService.calcExp(player, miningStat, exp);
        if(grade != 0) {
            if (floor <= 2) miningStatService.calcLowDust(player, miningStat, DUST_BASE.get(grade - 1));
            else miningStatService.calcHighDust(player, miningStat, DUST_BASE.get(grade - 5));
        }

        miningBuffService.consecutiveSpeed(player, miningStat.is_consecutiveSpeed());
        miningBuffService.consecutiveFortune(player, miningStat.is_consecutiveFortune());
        blockRegenService.getRegenBlock(player, miningStat, block, floor);
        blockSpreadService.applyBlockSpread(player, miningStat, block, floor);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!miningStatMap.containsKey(player.getUniqueId())) {
            miningStatMap.put(player.getUniqueId(), miningStatService.getCachedStat(player, 0));
            plugin.getLogger().info(player.getName() + "의 초기 채광 스텟 세팅 생성.");
        }
    }
}
