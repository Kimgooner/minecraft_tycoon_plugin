package org.kimgooner.tycoon.job.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.kimgooner.tycoon.global.item.ItemGlowUtil;

import java.util.Map;
import java.util.Random;


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
    public record DropData(ItemStack drop, int grade) {}

    private static final Map<Material, DropData> oreDropTable = Map.ofEntries(
        Map.entry(Material.COAL_ORE, new DropData(new ItemStack(Material.COAL), 1)),
        Map.entry(Material.COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT), 1)),
        Map.entry(Material.IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT), 2)),
        Map.entry(Material.GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT), 2)),
        Map.entry(Material.LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 4), 3)),
        Map.entry(Material.REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 4), 3)),
        Map.entry(Material.DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND), 4)),
        Map.entry(Material.EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD), 4)),

        Map.entry(Material.DEEPSLATE_COAL_ORE, new DropData(new ItemStack(Material.COAL, 2),1)),
        Map.entry(Material.DEEPSLATE_COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT, 2),1)),
        Map.entry(Material.DEEPSLATE_IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT, 2),2)),
        Map.entry(Material.DEEPSLATE_GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT, 2),2)),
        Map.entry(Material.DEEPSLATE_LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 8),3)),
        Map.entry(Material.DEEPSLATE_REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 8),3)),
        Map.entry(Material.DEEPSLATE_DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND, 2), 4)),
        Map.entry(Material.DEEPSLATE_EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD, 2), 4)),

        Map.entry(Material.COAL_BLOCK, new DropData(new ItemStack(Material.COAL, 4),1)),
        Map.entry(Material.WAXED_COPPER_BLOCK, new DropData(new ItemStack(Material.COPPER_INGOT, 4),1)),
        Map.entry(Material.IRON_BLOCK, new DropData(new ItemStack(Material.IRON_INGOT, 4),2)),
        Map.entry(Material.GOLD_BLOCK, new DropData(new ItemStack(Material.GOLD_INGOT, 4),2)),
        Map.entry(Material.LAPIS_BLOCK, new DropData(new ItemStack(Material.LAPIS_LAZULI, 16),3)),
        Map.entry(Material.REDSTONE_BLOCK, new DropData(new ItemStack(Material.REDSTONE, 16),3)),
        Map.entry(Material.DIAMOND_BLOCK, new DropData(new ItemStack(Material.DIAMOND, 4),4)),
        Map.entry(Material.EMERALD_BLOCK, new DropData(new ItemStack(Material.EMERALD, 4),4))
    );

    /*
    특정 블럭만 처리하도록
     */

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();

        if(!oreDropTable.containsKey(blockType)){
            return;
        }

        MiningDAO.MiningStats stats = miningDAO.getMiningStats(player);

        /*
        행운 수치 100마다 1개 추가 확정
        1~99의 수치는 확률로 변환
        ex) 330 -> 3개 확정, 30%로 1개 더
         */

        int fortune = stats.getFortune();
        int pristine = stats.getPristine();
        int guaranteed = fortune / 100;
        int chance = fortune % 100;

        Random random = new Random();
        int result = (random.nextInt(100) < chance) ? 1 : 0;
        result += guaranteed + 1;

        boolean isPristine = random.nextInt(100) + pristine >= 95;

        event.setDropItems(false);

        DropData dropData = oreDropTable.get(blockType);
        if(dropData == null){
            return;
        }

        ItemStack baseDrop = dropData.drop();
        ItemStack dropItem = baseDrop.clone();
        result *= dropItem.getAmount();
        dropItem.setAmount(1);

        int grade = dropData.grade();

        ItemMeta meta = dropItem.getItemMeta();
        if (meta != null) {
            Component display = Component.text(dropItem.getType().name().toLowerCase().replace("_", " ")).color(ItemGlowUtil.getDisplayColor(grade)).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(" x" + result).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            if(isPristine){
                display = Component.text("✧ 순수한 ").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false).append(display).append(Component.text("(+" + result*2 + ")").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            }
            display = display.append(Component.text(" By " + player.getName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.displayName(display);
            dropItem.setItemMeta(meta);
        }

        Location dropLoc = block.getLocation().add(0.5, 0.5, 0.5);
        Item itemEntity = block.getWorld().dropItem(dropLoc, dropItem);

        itemEntity.setPickupDelay(Integer.MAX_VALUE);
        itemEntity.customName(dropItem.displayName());
        itemEntity.setCustomNameVisible(true);

        ItemGlowUtil.applyGlowColor(itemEntity, grade);

        new BukkitRunnable() {
            @Override
            public void run() {
                itemEntity.remove();
            }
        }.runTaskLater(plugin, 30L);
    }
}
