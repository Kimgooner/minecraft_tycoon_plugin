package org.kimgooner.tycoon.job.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kimgooner.tycoon.db.dao.MiningDAO;

import java.util.Collection;
import java.util.Random;
import java.util.Set;


public class MiningEventHandler implements Listener {
    private final MiningDAO miningDAO;
    private final JavaPlugin plugin;

    public MiningEventHandler(MiningDAO miningDAO, JavaPlugin plugin) {
        this.miningDAO = miningDAO;
        this.plugin = plugin;
    }

    /*
    특정 블럭 목록
     */
    private static final Set<Material> oreBlocks = Set.of(
            Material.COAL_ORE,
            Material.COPPER_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_REDSTONE_ORE
    );

    /*
    특정 블럭만 처리하도록
     */

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if(!oreBlocks.contains(block.getType())){
            return;
        }

        player.sendMessage("광물 블럭, 드랍됨!");
        MiningDAO.MiningStats stats = miningDAO.getMiningStats(player);

        /*
        행운 수치 100마다 1개 추가 확정
        1~99의 수치는 확률로 변환
        ex) 330 -> 3개 확정, 30%로 1개 더
         */

        int fortune = stats.getFortune();
        //player.sendMessage("행운 스텟: " + fortune);

        int guaranteed = fortune / 100;
        int chance = fortune % 100;

        Random random = new Random();
        int result = (random.nextInt(100) < chance) ? 1 : 0;
        result += guaranteed + 1;

        event.setDropItems(false);

        /* 이전 코드
        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        for(ItemStack drop : drops){
            ItemStack newDrop = drop.clone();
            newDrop.setAmount(drop.getAmount() * result);

            Location dropLoc = block.getLocation().add(0.5, 0.5, 0.5);
            block.getWorld().dropItem(dropLoc, newDrop);
        }
         */

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        for(ItemStack drop : drops){
            Material dropType = drop.getType();
            int totalAmount = drop.getAmount() * result;

            ItemStack fakeItem = new ItemStack(dropType);
            ItemMeta meta = fakeItem.getItemMeta();

            if (meta == null) {
                return;
            }

            String display = dropType.name().toLowerCase().replace("_", " ") + " x" + totalAmount;
            Component name = Component.text(display, NamedTextColor.GRAY);
            meta.displayName(name);

            fakeItem.setItemMeta(meta);

            Location dropLoc = block.getLocation().add(0.5, 0.5, 0.5);
            Item itemEntity = block.getWorld().dropItem(dropLoc, fakeItem);

            itemEntity.setPickupDelay(Integer.MAX_VALUE); // 줍기 방지
            itemEntity.customName(name);
            itemEntity.setCustomNameVisible(true);
            itemEntity.setGlowing(true); // 시각 강조

            new BukkitRunnable() {
                @Override
                public void run() {
                    itemEntity.remove();
                }
            }.runTaskLater(plugin, 20L);
        }
    }
}
