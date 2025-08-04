package org.kimgooner.tycoon.job.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.db.dao.MiningDAO;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class MiningEventHandler implements Listener {
    private final MiningDAO miningDAO;
    private final DataStorageDAO dataStorageDAO;
    private final JavaPlugin plugin;

    public MiningEventHandler(MiningDAO miningDAO, DataStorageDAO dataStorageDAO, JavaPlugin plugin) {
        this.miningDAO = miningDAO;
        this.dataStorageDAO = dataStorageDAO;
        this.plugin = plugin;
    }

    /*
    특정 블럭 목록
     */
    public record DropData(ItemStack drop, int grade, int target) {}

    private static final Map<Material, DropData> oreDropTable = Map.ofEntries(
        Map.entry(Material.COAL_ORE, new DropData(new ItemStack(Material.COAL), 1, 0)),
        Map.entry(Material.COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT), 1, 1)),
        Map.entry(Material.IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT), 2,2)),
        Map.entry(Material.GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT), 2,3)),
        Map.entry(Material.REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 4), 3,4)),
        Map.entry(Material.LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 4), 3,5)),
        Map.entry(Material.EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD), 4,6)),
        Map.entry(Material.DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND), 4,7)),

        Map.entry(Material.DEEPSLATE_COAL_ORE, new DropData(new ItemStack(Material.COAL, 2),1, 0)),
        Map.entry(Material.DEEPSLATE_COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT, 2),1,1)),
        Map.entry(Material.DEEPSLATE_IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT, 2),2,2)),
        Map.entry(Material.DEEPSLATE_GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT, 2),2,3)),
        Map.entry(Material.DEEPSLATE_REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 8),3,4)),
        Map.entry(Material.DEEPSLATE_LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 8),3,5)),
        Map.entry(Material.DEEPSLATE_EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD, 2), 4,6)),
        Map.entry(Material.DEEPSLATE_DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND, 2), 4,7)),

        Map.entry(Material.COAL_BLOCK, new DropData(new ItemStack(Material.COAL, 4),1, 0)),
        Map.entry(Material.WAXED_COPPER_BLOCK, new DropData(new ItemStack(Material.COPPER_INGOT, 4),1,1)),
        Map.entry(Material.IRON_BLOCK, new DropData(new ItemStack(Material.IRON_INGOT, 4),2,2)),
        Map.entry(Material.GOLD_BLOCK, new DropData(new ItemStack(Material.GOLD_INGOT, 4),2,3)),
        Map.entry(Material.REDSTONE_BLOCK, new DropData(new ItemStack(Material.REDSTONE, 16),3,4)),
        Map.entry(Material.LAPIS_BLOCK, new DropData(new ItemStack(Material.LAPIS_LAZULI, 16),3,5)),
        Map.entry(Material.EMERALD_BLOCK, new DropData(new ItemStack(Material.EMERALD, 4),4,6)),
        Map.entry(Material.DIAMOND_BLOCK, new DropData(new ItemStack(Material.DIAMOND, 4),4,7))
    );

    /*
    특정 블럭만 처리하도록
     */

    private static final List<String> STAT_KEYS = List.of("power", "speed", "fortune", "pristine");
    private static final List<String> ENCHANT_KEYS = List.of("enchant_speed", "enchant_fortune", "enchant_pristine");
    private static final List<Integer> ENCHANT_FORTUNE = List.of(
            0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100
    );

    private static final List<Integer> ENCHANT_PRISTINE = List.of(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    );

    public int getStat(String key, Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if ((!STAT_KEYS.contains(key) && !ENCHANT_KEYS.contains(key)) || meta == null) return 0;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.getOrDefault(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, 0);
    }

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

        int enchant_fortune = getStat("enchant_fortune", player);
        int enchant_pristine = getStat("enchant_pristine", player);

        int fortune = stats.getFortune() + getStat("fortune", player) + ENCHANT_FORTUNE.get(enchant_fortune);
        int pristine = stats.getPristine() + getStat("pristine", player) + ENCHANT_PRISTINE.get(enchant_pristine);

        player.sendMessage("인챈트 행운: " + enchant_fortune);
        player.sendMessage("스텟 행운: " + stats.getFortune());
        player.sendMessage("장비 행운: " + getStat("fortune", player));
        player.sendMessage("인챈트 순수: " + enchant_pristine);
        player.sendMessage("스텟 순수: " + stats.getPristine());
        player.sendMessage("장비 순수: " + getStat("pristine", player));
        player.sendMessage("-------------------------");
        player.sendMessage("채광 행운:" + fortune + " 만큼 적용됨.");
        player.sendMessage("순수:" + pristine + " 만큼 적용됨.");

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
        int target = dropData.target();

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

        if(isPristine){
            result *= 3;
        }
        dataStorageDAO.addAmount(player, 1, target, result);

        new BukkitRunnable() {
            @Override
            public void run() {
                itemEntity.remove();
            }
        }.runTaskLater(plugin, 30L);
    }
}
