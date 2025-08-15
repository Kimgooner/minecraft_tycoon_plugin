package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlockRegenService {
    private final JavaPlugin plugin;

    public BlockRegenService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

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
    private Material getRandomMaterial(List<Material> materials) {
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

    private static int randomOneOrTwo() {
        return ThreadLocalRandom.current().nextInt(1, 3);
    }

    public void getRegenBlock(Player player, MiningStat playerMiningStat, Block block, int floor) {
        Location loc = block.getLocation();
        Block targetBlock = loc.getBlock();
        Bukkit.getScheduler().runTask(plugin, () -> targetBlock.setType(Material.BEDROCK));

        if(playerMiningStat.umbralOre()){
            //움브랄나이트 메소드 추가
            Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(Material.STONE), playerMiningStat.getRegen_time());
        }
        else if(playerMiningStat.riftOre()){
            //균열 광물 메소드 추가
            Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(Material.STONE), playerMiningStat.getRegen_time());
        }
        else {
            Map<Integer, Map<Integer, List<Material>>> currentMap = RESULT_ORE_ORES;
            if (playerMiningStat.blockFind()) currentMap = RESULT_ORE_BLOCKS;

            int type = randomOneOrTwo();
            if (!playerMiningStat.oreFind()) type = 0;

            int final_type = type;
            Map<Integer, Map<Integer, List<Material>>> finalMap = currentMap;
            Bukkit.getScheduler().runTaskLater(plugin, () -> targetBlock.setType(getOre(finalMap, floor, final_type)), playerMiningStat.getRegen_time()); // 3초 뒤 광물 변환
        }
    }
}
