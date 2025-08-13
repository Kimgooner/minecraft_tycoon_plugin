package org.kimgooner.tycoon.job.mining;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MiningUtil {
    private final JavaPlugin plugin;
    private final DataStorageDAO dataStorageDAO;
    private final MiningStatManager miningStatManager;

    private final Map<UUID, Boolean> chainBreaking;
    public MiningUtil(JavaPlugin plugin, MiningController miningController) {
        this.plugin = plugin;
        this.dataStorageDAO = miningController.getGlobalController().getGlobalDaoController().getDataStorageDAO();
        this.miningStatManager = miningController.getMiningStatManager();

        this.chainBreaking = miningController.getChainBreakingMap();
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------- //
    // 속도 관련
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    public void applyMiningSpeedStat(Player player, int speedStat) {
        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

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
    // ---------------------------------------------------------------------------------------------------------------------------------------------- //

    // ---------------------------------------------------------------------------------------------------------------------------------------------- //
    // 채광 드랍 관련
    public record DropData(ItemStack drop, int grade, int target, int exp) {}
    public final Map<Material, DropData> oreDropTable = Map.ofEntries(
            Map.entry(Material.STONE, new DropData(new ItemStack(Material.STONE), 0, 0, 1)),
            Map.entry(Material.COBBLESTONE, new DropData(new ItemStack(Material.STONE), 0, 0, 1)),
            Map.entry(Material.ANDESITE, new DropData(new ItemStack(Material.STONE), 0, 0, 1)),
            Map.entry(Material.STONE_BRICKS, new DropData(new ItemStack(Material.STONE), 0, 0, 2)),
            Map.entry(Material.POLISHED_ANDESITE, new DropData(new ItemStack(Material.STONE), 0, 0, 2)),

            Map.entry(Material.DEEPSLATE, new DropData(new ItemStack(Material.STONE, 2), 0, 0, 3)),
            Map.entry(Material.COBBLED_DEEPSLATE, new DropData(new ItemStack(Material.STONE, 2), 0, 0, 2)),
            Map.entry(Material.BASALT, new DropData(new ItemStack(Material.STONE, 2), 0, 0, 2)),
            Map.entry(Material.SMOOTH_BASALT, new DropData(new ItemStack(Material.STONE, 2), 0, 0, 2)),

            Map.entry(Material.DEEPSLATE_BRICKS, new DropData(new ItemStack(Material.STONE, 3), 0, 0, 4)),
            Map.entry(Material.DEEPSLATE_TILES, new DropData(new ItemStack(Material.STONE, 3), 0, 0, 4)),
            Map.entry(Material.CHISELED_DEEPSLATE, new DropData(new ItemStack(Material.STONE, 3), 0, 0, 4)),
            Map.entry(Material.CRACKED_DEEPSLATE_TILES, new DropData(new ItemStack(Material.STONE, 3), 0, 0, 4)),

            Map.entry(Material.COAL_ORE, new DropData(new ItemStack(Material.COAL), 1, 1, 6)),
            Map.entry(Material.COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT), 1, 2, 6)),
            Map.entry(Material.IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT), 2,3, 6)),
            Map.entry(Material.GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT), 2,4, 6)),
            Map.entry(Material.REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 4), 3,5, 7)),
            Map.entry(Material.LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 4), 3,6, 7)),
            Map.entry(Material.EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD), 4,7, 9)),
            Map.entry(Material.DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND), 4,8, 9)),

            Map.entry(Material.DEEPSLATE_COAL_ORE, new DropData(new ItemStack(Material.COAL),1, 1, 6)),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, new DropData(new ItemStack(Material.COPPER_INGOT),1,2, 6)),
            Map.entry(Material.DEEPSLATE_IRON_ORE, new DropData(new ItemStack(Material.IRON_INGOT),2,3, 6)),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, new DropData(new ItemStack(Material.GOLD_INGOT),2,4, 6)),
            Map.entry(Material.DEEPSLATE_REDSTONE_ORE, new DropData(new ItemStack(Material.REDSTONE, 4),3,5, 7)),
            Map.entry(Material.DEEPSLATE_LAPIS_ORE, new DropData(new ItemStack(Material.LAPIS_LAZULI, 4),3,6, 7)),
            Map.entry(Material.DEEPSLATE_EMERALD_ORE, new DropData(new ItemStack(Material.EMERALD), 4,7, 9)),
            Map.entry(Material.DEEPSLATE_DIAMOND_ORE, new DropData(new ItemStack(Material.DIAMOND), 4,8, 9)),

            Map.entry(Material.COAL_BLOCK, new DropData(new ItemStack(Material.COAL, 2),1, 1, 12)),
            Map.entry(Material.WAXED_COPPER_BLOCK, new DropData(new ItemStack(Material.COPPER_INGOT, 2),1, 2, 12)),
            Map.entry(Material.IRON_BLOCK, new DropData(new ItemStack(Material.IRON_INGOT, 2),2,3, 13)),
            Map.entry(Material.GOLD_BLOCK, new DropData(new ItemStack(Material.GOLD_INGOT, 2),2,4, 13)),
            Map.entry(Material.REDSTONE_BLOCK, new DropData(new ItemStack(Material.REDSTONE, 8),3,5, 14)),
            Map.entry(Material.LAPIS_BLOCK, new DropData(new ItemStack(Material.LAPIS_LAZULI, 8),3,6, 14)),
            Map.entry(Material.EMERALD_BLOCK, new DropData(new ItemStack(Material.EMERALD, 2),4,7, 20)),
            Map.entry(Material.DIAMOND_BLOCK, new DropData(new ItemStack(Material.DIAMOND, 2),4,8, 20))
    );
    private final List<Integer> DUST_BASE = List.of(
            2, 4, 6, 10
    );
    public boolean getDropItem(Player player, MiningStat playerMiningStat, Material material) {
        if (!oreDropTable.containsKey(material)) {
            return false;
        }
        DropData dropData = oreDropTable.get(material);

        ItemStack dropItem = dropData.drop();
        int base_amount = dropItem.getAmount();
        int grade = dropData.grade();
        int target =  dropData.target();
        int exp = dropData.exp();
        boolean isPristine = playerMiningStat.isPristine();

        int[] result_amount = new int[2];
        result_amount[0] = (playerMiningStat.calcFortune() + 1) * base_amount;
        if(isPristine) result_amount[0] *= 3;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            miningStatManager.calcExp(player, playerMiningStat, exp);
            if(grade != 0) {
                if (grade <= 4) miningStatManager.calcLowDust(player, playerMiningStat, DUST_BASE.get(grade-1));
                else miningStatManager.calcHighDust(player, playerMiningStat, DUST_BASE.get(grade - 5));
            }
            int finalResult = result_amount[0];
            dataStorageDAO.addAmount(player, 1, target, finalResult);
        });

//        ItemMeta meta = dropItem.getItemMeta();
//        if (meta != null) {
//            Component display = Component.text(dropItem.getType().name().toLowerCase().replace("_", " ")).color(ItemGlowUtil.getDisplayColor(grade)).decoration(TextDecoration.ITALIC, false)
//                    .append(Component.text(" x" + result_amount[0]).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
//            if(isPristine){
//                display = Component.text("✧ 순수한 ").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false).append(display).append(Component.text("(+" + result_amount[0] * 2 + ")").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
//            }
//            display = display.append(Component.text(" By " + player.getName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
//            meta.displayName(display);
//            dropItem.setItemMeta(meta);
//        }
//
//        Location dropLoc = targetBlock.getLocation().add(0.5, 0.5, 0.5);
//        Item itemEntity = targetBlock.getWorld().dropItem(dropLoc, dropItem);
//
//        itemEntity.setPickupDelay(Integer.MAX_VALUE);
//        itemEntity.customName(dropItem.displayName());
//        itemEntity.setCustomNameVisible(true);
//        ItemGlowUtil.applyGlowColor(itemEntity, grade);
//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                itemEntity.remove();
//            }
//        }.runTaskLater(plugin, 30L);

        return true;
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------- //

    // ---------------------------------------------------------------------------------------------------------------------------------------------- //
    // 광물 생성 관련
    private final Map<Integer, Map<Integer, List<Material>>> RESULT_ORE_ORES = Map.of( // 생성 광물
            1, Map.of(
                    0, List.of(Material.STONE, Material.ANDESITE, Material.COBBLESTONE),
                    1, List.of(Material.COAL_ORE, Material.COPPER_ORE),
                    2, List.of(Material.IRON_ORE, Material.GOLD_ORE)
            ),
            2, Map.of(
                    0, List.of(Material.STONE, Material.STONE_BRICKS, Material.POLISHED_ANDESITE),
                    1, List.of(Material.REDSTONE_ORE, Material.LAPIS_ORE),
                    2, List.of(Material.EMERALD_ORE, Material.DIAMOND_ORE)
            ),
            3, Map.of(
                    0, List.of(Material.DEEPSLATE, Material.COBBLED_DEEPSLATE, Material.BASALT, Material.SMOOTH_BASALT),
                    1, List.of(Material.PRISMARINE, Material.POLISHED_DIORITE),
                    2, List.of(Material.AMETHYST_BLOCK, Material.LIGHT_BLUE_GLAZED_TERRACOTTA)
            ),
            4, Map.of(
                    0, List.of(Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES, Material.CHISELED_DEEPSLATE, Material.CRACKED_DEEPSLATE_TILES),
                    1, List.of(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA),
                    2, List.of(Material.CYAN_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA)
            )
    );
    private final Map<Integer, Map<Integer, List<Material>>> RESULT_ORE_BLOCKS = Map.of( // 생성 광물
            1, Map.of(
                    0, List.of(Material.STONE, Material.ANDESITE, Material.COBBLESTONE),
                    1, List.of(Material.COAL_BLOCK, Material.WAXED_COPPER_BLOCK),
                    2, List.of(Material.IRON_BLOCK, Material.GOLD_BLOCK)
            ),
            2, Map.of(
                    0, List.of(Material.STONE, Material.STONE_BRICKS, Material.POLISHED_ANDESITE),
                    1, List.of(Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK),
                    2, List.of(Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK)
            ),
            3, Map.of(
                    0, List.of(Material.DEEPSLATE, Material.COBBLED_DEEPSLATE, Material.BASALT, Material.SMOOTH_BASALT),
                    1, List.of(Material.WAXED_OXIDIZED_COPPER, Material.QUARTZ_BLOCK),
                    2, List.of(Material.PURPLE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS)
            ),
            4, Map.of(
                    0, List.of(Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES, Material.CHISELED_DEEPSLATE, Material.CRACKED_DEEPSLATE_TILES),
                    1, List.of(Material.WHITE_STAINED_GLASS, Material.GRAY_STAINED_GLASS),
                    2, List.of(Material.CYAN_STAINED_GLASS, Material.BLUE_STAINED_GLASS)
            )
    );
    public Material getRandomMaterial(List<Material> materials) {
        int index = ThreadLocalRandom.current().nextInt(materials.size());
        return materials.get(index);
    }
    private Material getOre(Map<Integer, Map<Integer, List<Material>>> map, int floor, int type) { // 게이트 확률 지정
        return getRandomMaterial(map.get(floor).get(type));
    }
    private final Map<String, Integer> REGION_MAP = Map.of( // region 테이블
            "mine1", 1,
            "mine2", 2,
            "mine3", 3,
            "mine4", 4
    );
    public Integer isInTargetRegion(Location loc) { // 공간 여부
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
        ApplicableRegionSet regionSet = Objects.requireNonNull(regions).getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            return REGION_MAP.get(region.getId());
        }
        return 0;
    }
    public static int randomOneOrTwo() {
        return ThreadLocalRandom.current().nextInt(1, 3);
    }
    public void getRegenBlock(Player player, MiningStat playerMiningStat, Block block) {
        Location loc = block.getLocation();
        int floor = isInTargetRegion(loc);
        Block targetBlock = loc.getBlock();
        if(floor == 0){ return; }
        Bukkit.getScheduler().runTask(plugin, () -> targetBlock.setType(Material.BEDROCK));

        if(playerMiningStat.chestFind(floor)){
            if(playerMiningStat.highChestFind()){
                player.playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1, 1);
                player.sendMessage("§f[시스템] - §bHIGH CHEST FOUND!!! §e상위 보물 상자§f를 발견했습니다!!");
            }
            else {
                player.playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 1, 1);
                player.sendMessage("§f[시스템] - §eCHEST FOUND! §e보물 상자§f를 발견했습니다!!");
            }
        }
        Map<Integer, Map<Integer, List<Material>>> currentMap = RESULT_ORE_ORES;
        if (playerMiningStat.blockFind()) currentMap = RESULT_ORE_BLOCKS;

        int type = randomOneOrTwo();
        if(!playerMiningStat.oreFind()) type = 0;

        int final_type = type;
        Map<Integer, Map<Integer, List<Material>>> finalMap = currentMap;
        Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(getOre(finalMap, floor, final_type)), 60L); // 3초 뒤 광물 변환

        if(chainBreaking.getOrDefault(player.getUniqueId(), false)) return;
        applyBlockSpread(player, block, playerMiningStat.calcSpread());
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------- //

    // ---------------------------------------------------------------------------------------------------------------------------------------------- //
    // 연쇄 채광 관련
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

    int BATCH_SIZE = 3;
    public void applyBlockSpread(Player player, Block origin, int blockSpread) {
        if (blockSpread <= 0) return;

        List<Block> candidates = getNearbyValidBlocks(origin);
        Collections.shuffle(candidates);

        chainBreaking.put(player.getUniqueId(), true);

        UUID uuid = player.getUniqueId();
        int maxCount = Math.min(blockSpread, candidates.size());

        new BukkitRunnable() {
            int index = 0;
            int count = 0;

            @Override
            public void run() {
                int processedThisTick = 0;
                while (processedThisTick < BATCH_SIZE && count < maxCount && index < candidates.size()) {
                    Block target = candidates.get(index++);
                    if (isValidSpreadTarget(target)) {
                        Material material = target.getType();
                        target.setType(Material.AIR);
                        MiningStat miningStat = miningStatManager.getCachedStat(player);
                        boolean dropResult = getDropItem(player, miningStat, material);
                        getRegenBlock(player, miningStat, target);
                        count++;
                        if (!dropResult) return;
                    }
                    processedThisTick++;
                }
                if (count >= maxCount || index >= candidates.size()) {
                    chainBreaking.remove(uuid);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------- //
}
