package org.kimgooner.tycoon.global.item.job.mining;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.item.global.ItemUtil;

import java.util.HashMap;
import java.util.Map;

public class Pickaxe {
    private final ItemStack test;
    private final Map<Integer, ItemStack> pickaxes;

    private final CustomStack c1 = CustomStack.getInstance("mining:pick_1");
    private final CustomStack c2 = CustomStack.getInstance("mining:pick_2");
    private final CustomStack c3 = CustomStack.getInstance("mining:pick_3");
    private final CustomStack c4 = CustomStack.getInstance("mining:pick_4");

    public Pickaxe(JavaPlugin plugin) {
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

        pickaxes = new HashMap<>();

        if(c1 != null) {
            ItemStack pick = new PickaxeBuilder(plugin, c1.getItemStack())
                    .hideAttributeModifiers()
                    .unbreakable(true)
                    .displayName(Component.text("§f반쯤 부서진 미스릴 곡괭이"))
                    .setPower(1)
                    .setSpeed(150)
                    .setFortune(2)
                    .build();
            pickaxes.put(1, pick);
        }
        else plugin.getLogger().info("c1 아이템 확인 불가.");
        if(c2 != null) {
            ItemStack pick = new PickaxeBuilder(plugin, c2.getItemStack())
                    .hideAttributeModifiers()
                    .unbreakable(true)
                    .displayName(Component.text("§f붕대 감은 미스릴 곡괭이"))
                    .setPower(1)
                    .setSpeed(250)
                    .setFortune(4)
                    .build();
            pickaxes.put(2, pick);
        }
        else plugin.getLogger().info("c2 아이템 확인 불가.");
        if(c3 != null) {
            ItemStack pick = new PickaxeBuilder(plugin, c3.getItemStack())
                    .hideAttributeModifiers()
                    .unbreakable(true)
                    .displayName(Component.text("§a미스릴 곡괭이"))
                    .setPower(1)
                    .setSpeed(300)
                    .setFortune(7)
                    .build();
            pickaxes.put(3, pick);
        }
        else plugin.getLogger().info("c3 아이템 확인 불가.");
        if(c4 != null) {
            ItemStack pick = new PickaxeBuilder(plugin, c4.getItemStack())
                    .hideAttributeModifiers()
                    .unbreakable(true)
                    .displayName(Component.text("§a정제된 미스릴 곡괭이"))
                    .setPower(1)
                    .setSpeed(350)
                    .setFortune(10)
                    .build();
            pickaxes.put(4, pick);
        }
        else plugin.getLogger().info("c4 아이템 확인 불가.");
    }

    public ItemStack getTestPickaxe(Player player) {
        ItemStack item = test.clone();
        ItemUtil.appendCreatorLore(item, player);
        return item;
    }

    public void getPickaxe(Player player, int id) {
        ItemStack item = pickaxes.get(id).clone();
        ItemUtil.appendCreatorLore(item, player);
        player.give(item);
    }
}
