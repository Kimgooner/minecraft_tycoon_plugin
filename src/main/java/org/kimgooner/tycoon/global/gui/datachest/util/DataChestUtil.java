package org.kimgooner.tycoon.global.gui.datachest.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.util.List;

public class DataChestUtil {
    public static void populateItems(ChestGui gui, List<ItemStack> items, List<Integer> grades, List<Integer> amounts) {
        OutlinePane pane = new OutlinePane(0, 1, 9, 4);

        for(int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            Integer grade = grades.get(i);
            Integer amount = amounts.get(i);

            ItemStack element = new ItemBuilder(item)
                    .displayName(
                            item.displayName().color(ItemGlowUtil.getDisplayColor(grade)).decoration(TextDecoration.ITALIC, false)
                    )
                    .addLore(
                            Component.text("데이터 보유량: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                    .append(Component.text(String.format("%,d", amount)).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                                    .append(Component.text(" bits")).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                    )
                    .build();
            pane.addItem(new GuiItem(element));
        }
        gui.addPane(pane);
    }
}
