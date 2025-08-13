package org.kimgooner.tycoon.job.mining.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
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

    private boolean isPickaxe(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;

        Material type = item.getType();
        return type == Material.WOODEN_PICKAXE ||
                type == Material.STONE_PICKAXE ||
                type == Material.IRON_PICKAXE ||
                type == Material.GOLDEN_PICKAXE ||
                type == Material.DIAMOND_PICKAXE ||
                type == Material.NETHERITE_PICKAXE;
    }

    @EventHandler
    public void onBlockDamageCalc(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();

        // 현재 아이템이 곡괭이인지 확인
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!isPickaxe(mainHand)) {
            miningUtil.removeMiningSpeedModifier(player);
            return;
        }

        if (!miningUtil.oreDropTable.containsKey(material)) {
            event.setCancelled(true);  // 다른 블럭은 부수기 못 하게 막음
            event.getPlayer().sendMessage(Component.text("이 블럭은 부술 수 없습니다!").color(NamedTextColor.RED));
            return;
        }

        MiningStat miningOverall = miningStatManager.getCachedStat(player);
        miningStatMap.put(player.getUniqueId(), miningOverall);

//        miningStatMap.remove(player.getUniqueId()); // 일단 지우기
//        MiningStat miningOverall = miningStatManager.setStat(player);
//        miningStatMap.put(player.getUniqueId(), miningOverall); // 맵 남기기

        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        miningUtil.removeMiningSpeedModifier(player);
        miningUtil.applyMiningSpeedStat(player, miningOverall.getSpeed());

        // 디버그 메시지
//        double baseValue = attr.getBaseValue();
//        double totalValue = attr.getValue();
//        player.sendMessage(Component.text("기본 채광 속도: " + baseValue).color(NamedTextColor.GRAY));
//        player.sendMessage(Component.text("최종 채광 속도: " + totalValue).color(NamedTextColor.GRAY));
//        player.sendMessage(
//                Component.text("⸕ 채광 속도 적용됨 - ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
//                        .append(Component.text(String.format("%,d", miningOverall.getSpeed())).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
//        );
    }

    // 광물 드랍 시스템
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();

        if(editingMode.contains(player.getUniqueId())) {return;}

        MiningStat miningStat = miningStatMap.get(player.getUniqueId());

        if(!miningUtil.getDropItem(player, miningStat, block, material)) return;
        miningUtil.getRegenBlock(player, miningStat, block);
        miningPassiveBuff.consecutiveSpeed(player);
        miningPassiveBuff.consecutiveFortune(player);
        event.setDropItems(false);
    }
}
