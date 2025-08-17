package org.kimgooner.tycoon.global.item.global;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

/*
    //채광 스텟
    private int mining_power;
    private int mining_speed;
    private int mining_fortune;
    private int mining_spread;
    private int mining_dust;
    private double mining_wisdom;

    //채광 인챈트
    private int enchant_mining_efficiency;
    private int enchant_mining_fortune;
    private int enchant_mining_prismatic;
    private int enchant_mining_archeologist;
    private int enchant_mining_wisdom;
 */

@Getter
public class ItemStatCalculator {
    private final JavaPlugin plugin;

    public ItemStatCalculator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private record EnchantInfo(String enchantName, int value) {}
    private final Map<String, EnchantInfo> enchantments = Map.ofEntries(
            Map.entry("mining_speed", new EnchantInfo("efficiency", 20)),
            Map.entry("mining_fortune", new EnchantInfo("fortune", 5)),
            Map.entry("mining_dust", new EnchantInfo("archeologist", 2)),
            Map.entry("mining_wisdom", new EnchantInfo("miner", 2))
    );

    private PersistentDataContainer getSafePDC(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return new ItemStack(Material.STONE).getItemMeta().getPersistentDataContainer(); // 빈 PDC
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null ? meta.getPersistentDataContainer()
                : new ItemStack(Material.STONE).getItemMeta().getPersistentDataContainer();
    }

    private int getInt(PersistentDataContainer data, String key) {
        return data.getOrDefault(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, 0);
    }

    private double getDouble(PersistentDataContainer data, String key) {
        return data.getOrDefault(new NamespacedKey(plugin, key), PersistentDataType.DOUBLE, 0.0);
    }

    private int getStatInt(PersistentDataContainer data, String key) {
        if(!enchantments.containsKey(key)) return getInt(data, key);
        else return getInt(data, key) + (getInt(data, enchantments.get(key).enchantName) * enchantments.get(key).value);
    }

    private double getStatDouble(PersistentDataContainer data, String key) {
        if(!enchantments.containsKey(key)) return getDouble(data, key);
        else return getDouble(data, key) + (getDouble(data, enchantments.get(key).enchantName) * enchantments.get(key).value);
    }

    //----------------------------------------------------------------------------------
    //채광
    private ItemStat getStatMining(PersistentDataContainer data) {
        return ItemStat.builder()
                .mining_power(getStatInt(data, "mining_power"))
                .mining_speed(getStatInt(data, "mining_speed"))
                .mining_fortune(getStatInt(data, "mining_fortune"))
                .mining_spread(getStatInt(data, "mining_spread"))
                .mining_dust(getStatInt(data, "mining_dust"))
                .mining_wisdom(getStatInt(data, "mining_wisdom"))
                .build();
    }

    public ItemStat getStatMiningTotal(Player player) {
        List<ItemStack> items = List.of(
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand(),
                player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots()
        );
        ItemStat total = ItemStat.empty();

        for (ItemStack item : items) {
            total = total.add(getStatMining(getSafePDC(item)));
        }

        return total;
    }
    //----------------------------------------------------------------------------------
}
