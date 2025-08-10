package org.kimgooner.tycoon.global.gui.datachest.util;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.util.List;

public class DataChestUtil {
    public static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
             37, 38, 39, 40, 41, 42, 43
    };

    public static void populateItems(ChestGui gui, List<ItemStack> items, List<Integer> grades, List<String> names, List<Integer> amounts) {
        for(int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            Integer grade = grades.get(i);
            String name = names.get(i);
            Integer amount = amounts.get(i);

            ItemStack element = new ItemBuilder(item)
                    .displayName(
                            Component.text(name).color(ItemGlowUtil.getDisplayColor(grade)).decoration(TextDecoration.ITALIC, false)
                    )
                    .addLore(
                            Component.text("데이터 보유량: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                    .append(Component.text(String.format("%,d", amount)).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                                    .append(Component.text(" bits")).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                    )
                    .build();
            gui.getInventory().setItem(INNER_SLOTS[i], element);
        }
    }
}
