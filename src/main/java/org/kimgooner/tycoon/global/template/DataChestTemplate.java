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

public class DataChestTemplate {
    public static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public static Inventory createEmptyFrame(String title) {
        Inventory gui = Bukkit.createInventory(null, 45, Component.text(title).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        // 테두리를 유리로 채움
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(Component.text(" "))
                .build();

        for (int i = 0; i < 45; i++) {
            if (!isInnerSlot(i)) {
                gui.setItem(i, border);
            }
        }

        gui.setItem(40, ItemTemplate.getBackward());
        return gui;
    }

    public static boolean isInnerSlot(int index) {
        return Arrays.stream(INNER_SLOTS).anyMatch(i -> i == index);
    }
}
