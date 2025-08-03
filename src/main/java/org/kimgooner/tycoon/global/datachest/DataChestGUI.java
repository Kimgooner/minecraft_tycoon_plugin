package org.kimgooner.tycoon.global.datachest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.item.ItemBuilder;
import org.kimgooner.tycoon.global.template.DataChestCatergoryTemplate;

import java.util.List;

public class DataChestGUI {
    Inventory dataChestGUI = DataChestCatergoryTemplate.createEmptyFrame("데이터 보관함");

    ItemStack c1 = new ItemBuilder(new ItemStack(Material.IRON_INGOT))
            .displayName(Component.text("데이터 보관함 - 채광").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("채광을 통해 획득하는 아이템 데이터를").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("보관하는 데이터 보관함으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text(" "))
            .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .hideAttributeModifiers()
            .build();

    ItemStack c2 = new ItemBuilder(new ItemStack(Material.WHEAT))
            .displayName(Component.text("데이터 보관함 - 농사").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("농사를 통해 획득하는 아이템 데이터를").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("보관하는 데이터 보관함으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text(" "))
            .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .hideAttributeModifiers()
            .build();

    ItemStack c3 = new ItemBuilder(new ItemStack(Material.COD))
            .displayName(Component.text("데이터 보관함 - 낚시").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("낚시를 통해 획득하는 아이템 데이터를").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("보관하는 데이터 보관함으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text(" "))
            .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .hideAttributeModifiers()
            .build();

    ItemStack c4 = new ItemBuilder(new ItemStack(Material.ROTTEN_FLESH))
            .displayName(Component.text("데이터 보관함 - 전투").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("전투를 통해 획득하는 아이템 데이터를").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text("보관하는 데이터 보관함으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
            .addLore(Component.text(" "))
            .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .hideAttributeModifiers()
            .build();

    public void open(Player player){
        List<ItemStack> itemList = List.of(
            c1, c2, c3, c4
        );
        List<Integer> amountList = List.of(1,1,1,1);

        DataChestCatergoryTemplate.populateItems(dataChestGUI, itemList, amountList);
        player.openInventory(dataChestGUI);
    }
}
