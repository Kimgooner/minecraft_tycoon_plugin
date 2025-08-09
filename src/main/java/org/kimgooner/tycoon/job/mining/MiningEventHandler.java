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
import org.bukkit.block.BlockFace;
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
import org.kimgooner.tycoon.Tycoon;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.db.dao.MiningDAO;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class MiningEventHandler implements Listener {
    private final MiningDAO miningDAO;
    private final DataStorageDAO dataStorageDAO;
    private final Tycoon plugin;
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public MiningEventHandler(MiningDAO miningDAO, DataStorageDAO dataStorageDAO, JavaPlugin plugin) {
        this.miningDAO = miningDAO;
        this.dataStorageDAO = dataStorageDAO;
        this.plugin = (Tycoon) plugin;
    }

    /*
    특정 블럭 목록
     */
    public record DropData(ItemStack drop, int grade, int target) {}

    private static final Map<Material, DropData> oreDropTable = Map.ofEntries(
        Map.entry(Material.STONE, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.COBBLESTONE, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.ANDESITE, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.STONE_BRICKS, new DropData(new ItemStack(Material.STONE), 0, 0)),
        Map.entry(Material.POLISHED_ANDESITE, new DropData(new ItemStack(Material.STONE), 0, 0)),

        Map.entry(Material.DEEPSLATE, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),
        Map.entry(Material.COBBLED_DEEPSLATE, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),
        Map.entry(Material.BASALT, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),
        Map.entry(Material.SMOOTH_BASALT, new DropData(new ItemStack(Material.STONE, 2), 0, 0)),

        Map.entry(Material.DEEPSLATE_BRICKS, new DropData(new ItemStack(Material.STONE, 3), 0, 0)),
        Map.entry(Material.DEEPSLATE_TILES, new DropData(new ItemStack(Material.STONE, 3), 0, 0)),
        Map.entry(Material.CHISELED_DEEPSLATE, new DropData(new ItemStack(Material.STONE, 3), 0, 0)),
        Map.entry(Material.CRACKED_DEEPSLATE_TILES, new DropData(new ItemStack(Material.STONE, 3), 0, 0)),

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

    private final Map<String, Double> REGION_MAP_RESISTANCE = Map.of(
            "mine1", -0.30,
            "mine2", -0.50,
            "mine3", -0.70,
            "mine4", -0.90
    );

    private Double getRegionValue(Location loc) {
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
        ApplicableRegionSet regionSet = Objects.requireNonNull(regions).getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            return REGION_MAP_RESISTANCE.get(region.getId());
        }
        return 0.0;
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

        AttributeModifier multiplyModifier = new AttributeModifier(
                NamespacedKey.minecraft("region_value"),
                getRegionValue(player.getLocation()),
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
        );

        attr.addModifier(modifier);
        attr.addModifier(multiplyModifier);
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

                //Attribute 값 확인.
                double baseValue = attr.getBaseValue();
                double totalValue = attr.getValue();
                player.sendMessage(Component.text("기본 채광 속도: " + baseValue).color(NamedTextColor.GRAY));
                player.sendMessage(Component.text("최종 채광 속도: " + totalValue).color(NamedTextColor.GRAY));

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

    private static final Map<Integer, Map<Integer, Integer>> TIER_PROBABILITIES = Map.of(
            1, Map.of( // Mine 1의 게이트 확률
                    4, 5,
                    3, 10,
                    2, 25,
                    1, 60
            ),
            2, Map.of( // Mine 2의 게이트 확률
                    6, 3,
                    5, 5,
                    4, 7,
                    3, 15,
                    2, 25,
                    1, 45
            ),
            3, Map.of( // Mine 3의 게이트 확률
                    8, 2,
                    7, 3,
                    6, 5,
                    5, 7,
                    4, 10,
                    3, 15,
                    2, 25,
                    1, 33
            ),
            4, Map.of( // Mine 4의 게이트 확률
                    8, 5,
                    7, 6,
                    6, 8,
                    5, 10,
                    4, 15,
                    3, 15,
                    2, 20,
                    1, 21
            )
    );

    private final Map<Integer, Map<Integer, List<Material>>> RESULT_ORE = Map.of(
            0, Map.of(
                    1, List.of(Material.STONE, Material.ANDESITE, Material.COBBLESTONE),
                    2, List.of(Material.STONE, Material.STONE_BRICKS, Material.POLISHED_ANDESITE),
                    3, List.of(Material.DEEPSLATE, Material.COBBLED_DEEPSLATE, Material.BASALT, Material.SMOOTH_BASALT),
                    4, List.of(Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES, Material.CHISELED_DEEPSLATE, Material.CRACKED_DEEPSLATE_TILES)
            ),
            1, Map.of(
                    1, List.of(Material.COAL_ORE, Material.COPPER_ORE),
                    2, List.of(Material.IRON_ORE, Material.GOLD_ORE),
                    3, List.of(Material.REDSTONE_ORE, Material.LAPIS_ORE),
                    4, List.of(Material.EMERALD_ORE, Material.DIAMOND_ORE)
            ),
            2, Map.of(
                    1, List.of(Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_COPPER_ORE),
                    2, List.of(Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE),
                    3, List.of(Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_LAPIS_ORE),
                    4, List.of(Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_DIAMOND_ORE)
            ),
            3, Map.of(
                    1, List.of(Material.COAL_BLOCK, Material.WAXED_COPPER_BLOCK),
                    2, List.of(Material.IRON_BLOCK, Material.GOLD_BLOCK),
                    3, List.of(Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK),
                    4, List.of(Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK)
            )
    );

    public static boolean isSuccess(double chance) {
        return ThreadLocalRandom.current().nextDouble() < chance;
    }

    public static boolean isStone() {
        return isSuccess(0.5);
    }

    public static boolean isBlock() {
        return isSuccess(0.2);
    }

    public Material getRandomMaterial(List<Material> materials) {
        int index = ThreadLocalRandom.current().nextInt(materials.size());
        return materials.get(index);
    }

    private final Map<Integer, Integer> REGION_MAP_LIGHT = Map.of(
            1, 0,
            2, 500,
            3,1000,
            4,1500
    );

    private Material getOreByGateSystem(Integer floor, Integer type, Integer light) {
        if(type == 0){
            return getRandomMaterial(RESULT_ORE.get(type).get(floor));
        }
        else{
            double modifier = 1 + (light - REGION_MAP_LIGHT.get(floor)) * 0.2 * 0.01;
            for(int tier = TIER_PROBABILITIES.get(floor).size(); tier > 1; tier--){
                double chance = (double) TIER_PROBABILITIES.get(floor).getOrDefault(tier, 0);
                chance *= modifier; // modified value 값 들어갈 예정.
                plugin.getLogger().info(tier + " " + chance);
                if(ThreadLocalRandom.current().nextInt(100) < chance){
                    return getRandomMaterial(RESULT_ORE.get(type).get(tier));
                }
            }
        }
        return getRandomMaterial(RESULT_ORE.get(type).get(1));
    }

    private final Map<String, Integer> REGION_MAP = Map.of(
            "mine1", 1,
            "mine2", 2,
            "mine3", 3,
            "mine4", 4
    );

    private Integer isInTargetRegion(Location loc) {
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
        ApplicableRegionSet regionSet = Objects.requireNonNull(regions).getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            return REGION_MAP.get(region.getId());
        }
        return 0;
    }

    private boolean isChainBreaking = false;

    @EventHandler
    public void onBlockBreakInMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Block targetBlock = loc.getBlock();

        MiningDAO.MiningStats stats = miningDAO.getMiningStats(player);

        // 수정 모드
        if (plugin.getEditModePlayers().contains(player.getUniqueId())) {
            return;
        }

        // 특정 리전에 있을 때만 작동
        int floor = isInTargetRegion(loc);
        if(floor == 0){
            return;
        }

        int type = isStone() ? 0 : (isBlock() ? 3 : (floor >= 3 ? 2 : 1));
        Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(Material.BEDROCK), 1L); // 1틱 뒤 베드락
        Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(getOreByGateSystem(floor, type, stats.getLight())), 60L); // 3초 뒤 광물 변환

        if(isChainBreaking) { return; }
        player.sendMessage(Component.text("연쇄 파괴 이벤트 입장"));
        int spread = stats.getSpread();
        player.sendMessage(Component.text("연쇄 파괴 값: " + spread));
        applyBlockSpread(player, block, spread);
    }

    public List<Block> getNearbyValidBlocks(Block origin) {
        List<Block> list = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    // 자기 자신은 제외
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    Block target = origin.getRelative(dx, dy, dz);
                    if (isValidSpreadTarget(target)) {
                        list.add(target);
                    }
                }
            }
        }
        return list;
    }

    private boolean isValidSpreadTarget(Block block) {
        if (!oreDropTable.containsKey(block.getType())) return false;

        for (BlockFace face : BlockFace.values()) {
            if (block.getRelative(face).getType() == Material.AIR) {
                return true; // 최소 한 면이 AIR와 접해 있음
            }
        }

        return false;
    }

    public void applyBlockSpread(Player player, Block origin, int blockSpread) {
        if (blockSpread <= 0) return;

        int guaranteed = blockSpread / 100;
        int chance = blockSpread % 100;

        // 확률로 1개 더
        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            guaranteed++;
        }

        player.sendMessage(Component.text("갯수: " + guaranteed));

        // 주변 블록 가져오기
        List<Block> candidates = getNearbyValidBlocks(origin); // 반경 3 (7x7x7)
        Collections.shuffle(candidates); // 랜덤하게 섞기

        isChainBreaking = true;

        int count = 0;
        for (Block target : candidates) {
            if (count >= guaranteed) break;

            BlockBreakEvent fakeBreak = new BlockBreakEvent(target, player);
            Bukkit.getPluginManager().callEvent(fakeBreak);

            if (!fakeBreak.isCancelled()) {
                target.setType(Material.AIR);
                count++;
            }
        }

        isChainBreaking = false;
    }
}
