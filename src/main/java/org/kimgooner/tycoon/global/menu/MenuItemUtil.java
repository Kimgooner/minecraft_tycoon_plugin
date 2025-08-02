package org.kimgooner.tycoon.global.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MenuItemUtil {
    private static NamespacedKey key;

    public static void init(JavaPlugin plugin) {
        key = new NamespacedKey(plugin, "isMenuItem");
    }

    public static ItemStack createMenuItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text("[메뉴]").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(List.of(
                Component.text("우클릭하여 메뉴를 엽니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
        ));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.BYTE, (byte) 1);  // 태그 부여

        item.setItemMeta(meta);
        return item;
    }

    // 메뉴 아이템 여부 판단
    public static boolean isMenuItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(key, PersistentDataType.BYTE);
    }

    public static void enforceMenuItemSlot(Player player) {
        Inventory inv = player.getInventory();

        // 모든 슬롯에서 메뉴 아이템 제거 (특히 8번 슬롯도 포함)
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (isMenuItem(item)) {
                inv.clear(i);
            }
        }

        // 9번 슬롯에 새 메뉴 아이템 넣기
        inv.setItem(8, createMenuItem());
    }
}