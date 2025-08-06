package org.kimgooner.tycoon.job.mining;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
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
        Map.entry(Material.STONE, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.COBBLESTONE, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.ANDESITE, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.DEEPSLATE, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),
        Map.entry(Material.COBBLED_DEEPSLATE, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),
        Map.entry(Material.DEEPSLATE_BRICKS, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),
        Map.entry(Material.DEEPSLATE_TILES, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),

        Map.entry(Material.COAL_ORE, new DropData(new ItemStack(Material.COAL), 1, 1)),
        Map.entry(Material.COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT), 1, 2)),
        Map.entry(Material.IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT), 2,3)),
        Map.entry(Material.GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT), 2,4)),
        Map.entry(Material.REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 4), 3,5)),
        Map.entry(Material.LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 4), 3,6)),
        Map.entry(Material.EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD), 4,7)),
        Map.entry(Material.DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND), 4,8)),

        Map.entry(Material.DEEPSLATE_COAL_ORE, new DropData(new ItemStack(Material.COAL),1, 1)),
        Map.entry(Material.DEEPSLATE_COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT),1,2)),
        Map.entry(Material.DEEPSLATE_IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT),2,3)),
        Map.entry(Material.DEEPSLATE_GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT),2,4)),
        Map.entry(Material.DEEPSLATE_REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 4),3,5)),
        Map.entry(Material.DEEPSLATE_LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 4),3,6)),
        Map.entry(Material.DEEPSLATE_EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD), 4,7)),
        Map.entry(Material.DEEPSLATE_DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND), 4,8)),

        Map.entry(Material.COAL_BLOCK, new DropData(new ItemStack(Material.COAL, 2),1, 1)),
        Map.entry(Material.WAXED_COPPER_BLOCK, new DropData(new ItemStack(Material.COPPER_INGOT, 2),1,2)),
        Map.entry(Material.IRON_BLOCK, new DropData(new ItemStack(Material.IRON_INGOT, 2),2,3)),
        Map.entry(Material.GOLD_BLOCK, new DropData(new ItemStack(Material.GOLD_INGOT, 2),2,4)),
        Map.entry(Material.REDSTONE_BLOCK, new DropData(new ItemStack(Material.REDSTONE, 8),3,5)),
        Map.entry(Material.LAPIS_BLOCK, new DropData(new ItemStack(Material.LAPIS_LAZULI, 8),3,6)),
        Map.entry(Material.EMERALD_BLOCK, new DropData(new ItemStack(Material.EMERALD, 2),4,7)),
        Map.entry(Material.DIAMOND_BLOCK, new DropData(new ItemStack(Material.DIAMOND, 2),4,8))
    );

    /*
    특정 블럭만 처리하도록
     */

    private static final List<String> STAT_KEYS = List.of("power", "speed", "fortune", "pristine");
    private static final List<String> ENCHANT_KEYS = List.of("enchant_speed", "enchant_fortune", "enchant_pristine");
    private static final List<Integer> ENCHANT_EFFICIENCY = List.of(
            0, 30, 50, 70, 90, 110, 130, 150, 170, 190, 210
    );
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

    public void applyMiningSpeedStat(Player player, int speedStat) {
        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        // 중복 방지: 기존 modifier 제거
        /*
        for (AttributeModifier mod : attr.getModifiers()) {
            if (mod.getName().equals("customBreakSpeed")) {
                attr.removeModifier(mod);
                break;
            }
        }
         */

        // 스탯 1당 0.2 적용
        double bonus = speedStat * 0.01;

        AttributeModifier modifier = new AttributeModifier(
                NamespacedKey.minecraft("custom_break_speed"),
                bonus,
                AttributeModifier.Operation.ADD_SCALAR
        );

        attr.addModifier(modifier);
    }

    public void removeMiningSpeedModifier(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        for (AttributeModifier mod : attr.getModifiers()) {
            attr.removeModifier(mod);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();

        ItemStack item = player.getInventory().getItemInMainHand();
        if(!isPickaxe(item)) return;

        if (!oreDropTable.containsKey(material)) {
            event.setCancelled(true);  // 다른 블럭은 부수기 못 하게 막음
            event.getPlayer().sendMessage(Component.text("이 블럭은 부술 수 없습니다!").color(NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // 1틱 뒤에 실행 (아이템 장착 후 적용되도록)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack mainHand = player.getInventory().getItemInMainHand();

            AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
            if (attr == null) return;

            // 기존 modifier 제거
            removeMiningSpeedModifier(player);

            if (isPickaxe(mainHand)) {
                MiningDAO.MiningStats stats = miningDAO.getMiningStats(player);
                int speedStat = stats.getSpeed() + getStat("speed", player) + ENCHANT_EFFICIENCY.get(getStat("enchant_speed", player)); // <- 이건 너가 구현한 메서드
                applyMiningSpeedStat(player, speedStat);
                player.sendMessage(
                        Component.text("⸕ 채광 속도 적용됨 - ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text(String.format("%,d",  speedStat)).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                );
            } else {
                player.sendMessage(Component.text("⸕ 채광 속도 적용 해제").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            }
        }, 1L);
    }

    // 광물 드랍 시스템
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

        /* 디버그 콘솔

        player.sendMessage("인챈트 행운: " + enchant_fortune);
        player.sendMessage("스텟 행운: " + stats.getFortune());
        player.sendMessage("장비 행운: " + getStat("fortune", player));
        player.sendMessage("인챈트 순수: " + enchant_pristine);
        player.sendMessage("스텟 순수: " + stats.getPristine());
        player.sendMessage("장비 순수: " + getStat("pristine", player));
        player.sendMessage("-------------------------");
        player.sendMessage("채광 행운:" + fortune + " 만큼 적용됨.");
        player.sendMessage("순수:" + pristine + " 만큼 적용됨.");

         */

        int guaranteed = fortune / 100;
        int chance = fortune % 100;

        Random random = new Random();
        int[] result = new int[1];
        result[0] = (random.nextInt(100) < chance) ? 1 : 0;
        result[0] += guaranteed + 1;

        boolean isPristine = random.nextInt(100) + pristine >= 95;

        event.setDropItems(false);

        DropData dropData = oreDropTable.get(blockType);
        if(dropData == null){
            return;
        }

        ItemStack baseDrop = dropData.drop();
        ItemStack dropItem = baseDrop.clone();
        result[0] *= dropItem.getAmount();
        dropItem.setAmount(1);

        int grade = dropData.grade();
        int target = dropData.target();

        ItemMeta meta = dropItem.getItemMeta();
        if (meta != null) {
            Component display = Component.text(dropItem.getType().name().toLowerCase().replace("_", " ")).color(ItemGlowUtil.getDisplayColor(grade)).decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(" x" + result[0]).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            if(isPristine){
                display = Component.text("✧ 순수한 ").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false).append(display).append(Component.text("(+" + result[0] * 2 + ")").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
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
            result[0] *= 3;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int finalResult = result[0];
            dataStorageDAO.addAmount(player, 1, target, finalResult);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                itemEntity.remove();
            }
        }.runTaskLater(plugin, 30L);
    }

    /*
    돌 -> 50%
    광물 -> 50%
        일반 -> 35%
        딥 -> 10%
        블럭 -> 5%
     */

    private final Map<Integer, List<Integer>> backAndFrontWeight = Map.of(
            1, List.of(35, 15),
            2, List.of(25, 25),
            3, List.of(15, 35)
    );

    private boolean getWeightedResult(int key) {
        List<Integer> weights = backAndFrontWeight.getOrDefault(key, List.of(1, 1));
        int totalWeight = weights.get(0) + weights.get(1);
        int rand = new Random().nextInt(totalWeight);

        return rand < weights.get(0);
    }

    private boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

    private final int[] WEIGHTS = {40, 40, 10, 10};
    private final Map<Integer, List<Material>> gradesFront = Map.of(
            1, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK),
            2, List.of(Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK),
            3, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK),
            4, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK)
    );
    private final Map<Integer, List<Material>> gradesBack = Map.of(
            1, List.of(Material.IRON_ORE, Material.GOLD_ORE, Material.IRON_BLOCK, Material.GOLD_BLOCK),
            2, List.of(Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK),
            3, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK),
            4, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK)
    );

    private final int[] WEIGHTS_STONE = {10, 10, 10, 10};
    private final Map<Integer, List<Material>> gradesStone = Map.of(
            1, List.of(Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.STONE),
            2, List.of(Material.DEEPSLATE, Material.COBBLED_DEEPSLATE, Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES),
            3, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK),
            4, List.of(Material.COAL_ORE, Material.COPPER_ORE, Material.COAL_BLOCK, Material.COPPER_BLOCK)
    );

    private Material getWeightedBlockFromCategory(Map<Integer, List<Material>> categoryBlocks, Integer category, int[] weightList) {
        List<Material> list = categoryBlocks.getOrDefault(category, List.of(Material.STONE));
        if (list.size() != weightList.length) return Material.STONE; // 길이 안 맞으면 기본값 반환

        int totalWeight = 100;

        Random random = new Random();
        int rand = random.nextInt(totalWeight);
        int cumulative = 0;

        for (int i = 0; i < weightList.length; i++) {
            cumulative += weightList[i];
            if (rand < cumulative) {
                return list.get(i);
            }
        }

        return list.getFirst(); // fallback
    }

    //광산 광물 재생 시스템
    @EventHandler
    public void onBlockBreakInMine(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Block targetBlock = loc.getBlock();

        int[] mine = isInTargetRegion(loc);
        // 특정 리전에 있을 때만 작동

        if(mine[0] == 0 || mine[1] == 0){
            return;
        }

        int floor = mine[0];
        int diff = mine[1];

        Material result;
        if(getRandomBoolean()) {
            if (getWeightedResult(diff)) {
                result = getWeightedBlockFromCategory(gradesFront, floor, WEIGHTS);
            } else {
                result = getWeightedBlockFromCategory(gradesBack, floor, WEIGHTS);
            }
        }
        else{
            result = getWeightedBlockFromCategory(gradesStone, floor, WEIGHTS_STONE);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(Material.BEDROCK), 1L); // 1틱 뒤 베드락
        Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(result), 60L); // 3초 뒤 광물 변환
    }

    private int[] isInTargetRegion(Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if (regions == null) return new int[]{0, 0};

        ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            int start = 0, end = 0;
            if (region.getId().startsWith("mine_1")){
                start = 1;
            }
            if (region.getId().startsWith("mine_2")){
                start = 2;
            }
            if (region.getId().startsWith("mine_3")){
                start = 3;
            }
            if (region.getId().startsWith("mine_4")){
                start = 4;
            }

            if (region.getId().endsWith("_1")) {
                end = 1;
            }
            if (region.getId().endsWith("_2")) {
                end = 2;
            }
            if (region.getId().endsWith("_3")) {
                end = 3;
            }
            return  new int[]{start, end};
        }
        return new int[]{0, 0};
    }
}
