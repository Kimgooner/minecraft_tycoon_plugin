package org.kimgooner.tycoon.global.item.global;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    public static ItemStack createPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setOwningPlayer(player);
            // 기타 설정
            head.setItemMeta(meta);
        }

        return head;
    }

    public static ItemStack appendCreatorLore(ItemStack item, Player player) {
        if (item == null || player == null) return item;
        if (!item.hasItemMeta()) return item;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        Component signatureLine = Component.text("By ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(player.getName()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));

        boolean alreadyAppended = lore.stream().anyMatch(line -> line.contains(Component.text("By ")));
        if (!alreadyAppended) {
            lore.add(Component.text(" ")); // 빈 줄 구분
            lore.add(signatureLine);
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}