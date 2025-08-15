package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.Map;

public class MiningDropService {
    private final JavaPlugin plugin;
    private final DataStorageDAO dataStorageDAO;

    public MiningDropService(JavaPlugin plugin,  DataStorageDAO dataStorageDAO) {
        this.plugin = plugin;
        this.dataStorageDAO = dataStorageDAO;
    }

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
    public record dropResultData(Integer exp, Integer grade) {}
    public dropResultData getDropItem(Player player, MiningStat playerMiningStat, Material material, int floor) {
        DropData dropData = oreDropTable.get(material);

        ItemStack dropItem = dropData.drop();
        int base_amount = dropItem.getAmount();
        int grade = dropData.grade();
        int target =  dropData.target();
        int exp = dropData.exp();

        if(playerMiningStat.chestFind(floor)){
            if(playerMiningStat.highChestFind()){
                player.playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1, 1);
                player.sendMessage("§f[시스템] - §bHIGH CHEST FOUND!!! §e상위 보물 상자§f를 발견했습니다!!");
                // 상위 상자 드랍 메서드 추가
            }
            else {
                player.playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 1, 1);
                player.sendMessage("§f[시스템] - §eCHEST FOUND! §e보물 상자§f를 발견했습니다!!");
                // 상자 드랍 메서드 추가
            }
        }

        int[] result_amount = new int[2];
        result_amount[0] = (playerMiningStat.calcFortune() + 1) * base_amount;

        int finalResult = result_amount[0];
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dataStorageDAO.addAmount(player, 1, target, finalResult));
        return new dropResultData(exp, grade);
    }
}
