package org.kimgooner.tycoon.global.template;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;

public class ItemTemplate {
    public static ItemStack getBackward(){
        return new ItemBuilder(new ItemStack(Material.BARRIER))
                .displayName(Component.text("뒤로가기").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .hideAttributeModifiers()
                .build();
    }
}
