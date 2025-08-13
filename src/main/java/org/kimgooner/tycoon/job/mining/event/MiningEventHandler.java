package org.kimgooner.tycoon.job.mining.event;

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
import org.kimgooner.tycoon.job.mining.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class MiningEventHandler implements Listener {
    private final MiningUtil miningUtil;

    private final Set<UUID> editingMode;
    private final Map<UUID, MiningStat> miningStatMap;
    private final MiningStatManager miningStatManager;
    private final MiningPassiveBuff miningPassiveBuff;

    public MiningEventHandler(JavaPlugin plugin, MiningController miningController) {
        this.miningUtil = miningController.getMiningUtil();
        this.editingMode = miningController.getEditingMode();
        this.miningStatMap = miningController.getMiningMap();
        this.miningStatManager = miningController.getMiningStatManager();
        this.miningPassiveBuff = miningController.getMiningPassiveBuff();
    }

    @EventHandler
    public void onBlockDamageCalc(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();

        if (!miningUtil.oreDropTable.containsKey(material)) {
            event.setCancelled(true);  // 다른 블럭은 부수기 못 하게 막음
            return;
        }

        MiningStat miningOverall = miningStatManager.getCachedStat(player);
        miningStatMap.put(player.getUniqueId(), miningOverall);

        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        miningUtil.removeMiningSpeedModifier(player);
        miningUtil.applyMiningSpeedStat(player, miningOverall.getSpeed());
    }

    // 광물 드랍 시스템
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        int floor = miningUtil.isInTargetRegion(loc);
        Material material = block.getType();

        if(editingMode.contains(player.getUniqueId())) {return;}

        MiningStat miningStat = miningStatMap.get(player.getUniqueId());

        if(!miningUtil.getDropItem(player, miningStat, material)) return;
        miningUtil.getRegenBlock(player, miningStat, block);
        miningPassiveBuff.consecutiveSpeed(player);
        miningPassiveBuff.consecutiveFortune(player);
        event.setDropItems(false);
    }
}
