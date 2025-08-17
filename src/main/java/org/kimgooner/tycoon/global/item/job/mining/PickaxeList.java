package org.kimgooner.tycoon.global.item.job.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.item.global.ItemUtil;

public class PickaxeList {
    private final ItemStack test;

    public PickaxeList(JavaPlugin plugin) {
        this.test = new PickaxeBuilder(plugin, Material.DIAMOND_PICKAXE)
                .hideAttributeModifiers()
                .unbreakable(true)
                .displayName(Component.text("테스트용").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                .setPower(1)
                .setSpeed(1)
                .setFortune(1)
                .setSpread(1)
                .setDust(1)
                .setWisdom(1)
                .enchantEfficiency(1)
                .enchantFortune(4)
                .enchantArcheologist(8)
                .enchantWiseMiner(10)
                .hideAttributeModifiers()
                .unbreakable(true)
                .build();
    }

    public ItemStack getTestPickaxe(Player player) {
        ItemStack item = test.clone();
        ItemUtil.appendCreatorLore(item, player);
        return item;
    }
}
