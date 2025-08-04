package org.kimgooner.tycoon.global.item.job.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PickaxeBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final Plugin plugin;

    // 스탯 관련 데이터
    private static final List<String> STAT_KEYS = List.of("power", "speed", "fortune", "pristine");
    private static final Map<String, String> STAT_NAMES = Map.of(
            "power", "파괴 등급",
            "speed", "채광 속도",
            "fortune", "채광 행운",
            "pristine", "순수"
    );
    private static final Map<String, String> STAT_ICONS = Map.of(
            "power",   "Ⓟ ",  // 폭이 작음
            "speed",   "⸕  ", // 폭이 큼
            "fortune", "☘  ",
            "pristine","✧  "  // 폭이 좁음, 공백 더 줌
    );
    private static final Map<String, NamedTextColor> STAT_COLORS = Map.of(
            "power", NamedTextColor.GREEN,
            "speed", NamedTextColor.GOLD,
            "fortune", NamedTextColor.GOLD,
            "pristine", NamedTextColor.DARK_PURPLE
    );

    // 인챈트 관련 데이터
    private static final List<String> ENCHANT_KEYS = List.of("enchant_speed", "enchant_fortune", "enchant_pristine");
    private static final Map<String, String> ENCHANT_NAMES = Map.of(
            "enchant_speed", "효율",
            "enchant_fortune", "행운",
            "enchant_pristine", "순수"
    );
    private static final List<String> ENCHANT_ICONS = List.of(
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    );
    private static final List<NamedTextColor> ENCHANT_COLORS = List.of(
            NamedTextColor.DARK_GRAY,
            NamedTextColor.GRAY,
            NamedTextColor.WHITE,
            NamedTextColor.GREEN,
            NamedTextColor.BLUE,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.AQUA,
            NamedTextColor.RED
    );
    private static final List<Integer> ENCHANT_EFFICIENCY = List.of(
            0, 30, 50, 70, 90, 110, 130, 150, 170, 190, 210
    );

    private static final List<Integer> ENCHANT_FORTUNE = List.of(
            0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100
    );

    private static final List<Integer> ENCHANT_PRISTINE = List.of(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    );

    public PickaxeBuilder(Plugin plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.item = itemStack;
        this.meta = item.getItemMeta();
    }

    public PickaxeBuilder(Plugin plugin, Material material) {
        this.plugin = plugin;
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public PickaxeBuilder displayName(Component name) {
        if (meta != null) {
            meta.displayName(name);
        }
        return this;
    }

    public PickaxeBuilder unbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    public PickaxeBuilder hideAttributeModifiers() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        return this;
    }

    public PickaxeBuilder setPower(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setSpeed(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "speed"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setFortune(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "fortune"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setPristine(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "pristine"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setEnchantSpeed(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "enchant_speed"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setEnchantFortune(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "enchant_fortune"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setEnchantPristine(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "enchant_pristine"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    /*
    public PickaxeBuilder setStat(String key, int value) {
        if (!STAT_KEYS.contains(key)) return this;
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, value);
        }
        return this;
    }
     */

    private int getStat(String key) {
        if ((!STAT_KEYS.contains(key) && !ENCHANT_KEYS.contains(key)) || meta == null) return 0;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.getOrDefault(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, 0);
    }

    private List<Component> generateStatLore() {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.text(" "));
        lines.add(Component.text("[장비 스텟]").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        int enchantCount = 0;
        for (String key : STAT_KEYS) {
            int baseValue = getStat(key);
            int enchantLevel = getStat("enchant_" + key);
            int bonusValue = getEnchantBonus(key, enchantLevel);

            Component statLine = Component.text("")
                    .append(Component.text(STAT_NAMES.get(key) + ":")
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false))
                    .append(Component.text(String.format(" %,d ", baseValue))
                            .color(STAT_COLORS.get(key))
                            .decoration(TextDecoration.ITALIC, false));

            if (enchantLevel > 0 && bonusValue > 0) {
                statLine = statLine.append(Component.text(String.format("(+%,d) ", bonusValue))
                        .color(NamedTextColor.BLUE)
                        .decoration(TextDecoration.ITALIC, false));
                enchantCount++;
            }

            lines.add(statLine);
        }

        lines.add(Component.text(" "));
        lines.add(Component.text("[인챈트]").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        if (enchantCount > 0) {
            for (String key : ENCHANT_KEYS) {
                int level = getStat(key);
                if (level > 0) {
                    lines.add(Component.text(ENCHANT_NAMES.get(key) + " ")
                            .color(NamedTextColor.WHITE)
                            .decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(ENCHANT_ICONS.get(level))
                                    .color(ENCHANT_COLORS.get(level))
                                    .decoration(TextDecoration.ITALIC, false))
                    );
                }
            }
        } else {
            lines.add(Component.text("아무런 인챈트가 없습니다!")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }

        return lines;
    }

    private int getEnchantBonus(String key, int level) {
        switch (key) {
            case "speed":
                return ENCHANT_EFFICIENCY.get(level);
            case "fortune":
                return ENCHANT_FORTUNE.get(level);
            case "pristine":
                return ENCHANT_PRISTINE.get(level);
            default:
                return 0;
        }
    }

    public ItemStack build() {
        if (meta != null) {
            List<Component> fullLore = new ArrayList<>(generateStatLore());
            meta.lore(fullLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
