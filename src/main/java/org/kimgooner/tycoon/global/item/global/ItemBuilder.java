package org.kimgooner.tycoon.global.item.global;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final List<Component> loreComponents = new ArrayList<>();

    public ItemBuilder(ItemStack itemStack) {
        this.item = itemStack;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder displayName(Component name) {
        if (meta != null) {
            meta.displayName(name);
        }
        return this;
    }

    public ItemBuilder addLore(Component loreLine) {
        loreComponents.add(loreLine);
        return this;
    }

    public ItemBuilder lore(List<Component> loreLines) {
        loreComponents.clear();
        loreComponents.addAll(loreLines);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        if (meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }

    public ItemBuilder hideAttributeModifiers() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            if (!loreComponents.isEmpty()) {
                meta.lore(loreComponents);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
