package org.kimgooner.tycoon.global.template;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;

import java.util.Arrays;
import java.util.List;

public class DataChestCatergoryTemplate {
    public static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16
    };

    public static Inventory createEmptyFrame(String title) {
        Inventory gui = Bukkit.createInventory(null, 27, Component.text(title).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        // 테두리를 유리로 채움
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(Component.text(" "))
                .build();

        for (int i = 0; i < 27; i++) {
            if (!isInnerSlot(i)) {
                gui.setItem(i, border);
            }
        }

        gui.setItem(22, ItemTemplate.getBackward());
        return gui;
    }

    public static boolean isInnerSlot(int index) {
        return Arrays.stream(INNER_SLOTS).anyMatch(i -> i == index);
    }

    public static void populateItems(Inventory gui, List<ItemStack> items, List<Integer> amounts) {
        for (int i = 0; i < Math.min(items.size(), INNER_SLOTS.length); i++) {
            ItemStack item = items.get(i).clone();
            gui.setItem(INNER_SLOTS[i], item);
        }
    }
}
