package org.kimgooner.tycoon.global.item.job.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PickaxeBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final JavaPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PickaxeBuilder(JavaPlugin plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.item = itemStack;
        this.meta = item.getItemMeta();
    }

    public PickaxeBuilder(JavaPlugin plugin, Material material) {
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
            data.set(new NamespacedKey(plugin, "mining_power"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setSpeed(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "mining_speed"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setFortune(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "mining_fortune"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setSpread(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "mining_spread"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setDust(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "mining_dust"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder setWisdom(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "mining_wisdom"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder enchantEfficiency(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "efficiency"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder enchantFortune(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "fortune"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder enchantArcheologist(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "archeologist"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public PickaxeBuilder enchantWiseMiner(int value) {
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(plugin, "miner"), PersistentDataType.INTEGER, value);
        }
        return this;
    }

    private final List<String> ENCHANT_ICONS = List.of(
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    );
    private Component getEnchantText(int level, String name){
        if(level <= 3){
            return mm.deserialize("<white>%s %s</white>".formatted(name, ENCHANT_ICONS.get(level-1)));
        }
        else if (level <= 6){
            return mm.deserialize("<gold>%s %s</gold>".formatted(name, ENCHANT_ICONS.get(level-1)));
        }
        else if (level <= 9) {
            return mm.deserialize("<light_purple>%s %s</light_purple>".formatted(name, ENCHANT_ICONS.get(level-1)));
        }
        else {
            return mm.deserialize("<#e6fffe>%s %s</#e6fffe>".formatted(name, ENCHANT_ICONS.get(level-1)));
        }
    }

    public List<Component> buildLore(JavaPlugin plugin) {
        List<Component> lore = new ArrayList<>();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // 값 불러오기 (없으면 기본값 0)
        int power   = data.getOrDefault(new NamespacedKey(plugin, "mining_power"), PersistentDataType.INTEGER, 0);
        int speed   = data.getOrDefault(new NamespacedKey(plugin, "mining_speed"), PersistentDataType.INTEGER, 0);
        int fortune = data.getOrDefault(new NamespacedKey(plugin, "mining_fortune"), PersistentDataType.INTEGER, 0);
        int spread  = data.getOrDefault(new NamespacedKey(plugin, "mining_spread"), PersistentDataType.INTEGER, 0);
        int dust    = data.getOrDefault(new NamespacedKey(plugin, "mining_dust"), PersistentDataType.INTEGER, 0);
        int wisdom  = data.getOrDefault(new NamespacedKey(plugin, "mining_wisdom"), PersistentDataType.INTEGER, 0);

        int eff     = data.getOrDefault(new NamespacedKey(plugin, "efficiency"), PersistentDataType.INTEGER, 0);
        int fort    = data.getOrDefault(new NamespacedKey(plugin, "fortune"), PersistentDataType.INTEGER, 0);
        int arch    = data.getOrDefault(new NamespacedKey(plugin, "archeologist"), PersistentDataType.INTEGER, 0);
        int miner   = data.getOrDefault(new NamespacedKey(plugin, "miner"), PersistentDataType.INTEGER, 0);

        speed += eff * 20;
        fortune += fort * 5;
        dust += arch * 2;
        wisdom += miner * 2;

        int stat_count = 0;
        int special_count = 0;
        int enchant_count = 0;

        // 불러온 값으로 로어 만들기
        if (power > 0)   lore.add(Component.text("§f파괴 단계 %d".formatted(power)));
        lore.add(Component.text(" "));
        lore.add(Component.text("§f<스텟>"));
        if (speed > 0)   {lore.add(Component.text("§f채광 속도: §6+%,d ⸕".formatted(speed))); stat_count++;}
        if (fortune > 0) {lore.add(Component.text("§f채광 행운: §6+%,d ☘".formatted(fortune))); stat_count++;}
        if (spread > 0)  {lore.add(Component.text("§f연쇄 파괴: §e+%,d ▚".formatted(spread))); stat_count++;}
        if(stat_count == 0) lore.add(Component.text("§8-스텟 없음-"));
        lore.add(Component.text(" "));
        lore.add(Component.text("§f<특수 스텟>"));
        if (wisdom > 0)  {lore.add(Component.text("§f채광 숙련: §3+%,d ☯".formatted(wisdom))); special_count++;}
        if (dust > 0)    {lore.add(Component.text("§f추가 가루 획득량: §2+%,d%%".formatted(dust))); special_count++;}
        if(special_count == 0) lore.add(Component.text("§8-특수 스텟 없음-"));
        lore.add(Component.text(" "));
        lore.add(Component.text("§f<인챈트>"));
        if (eff > 0)     {lore.add(getEnchantText(eff, "효율").decoration(TextDecoration.ITALIC, false)); enchant_count++;}
        if (fort > 0)    {lore.add(getEnchantText(fort, "행운").decoration(TextDecoration.ITALIC, false)); enchant_count++;}
        if (arch > 0)    {lore.add(getEnchantText(arch, "고고학").decoration(TextDecoration.ITALIC, false)); enchant_count++;}
        if (miner > 0)   {lore.add(getEnchantText(miner, "숙련된 광부").decoration(TextDecoration.ITALIC, false)); enchant_count++;}
        if(enchant_count == 0) lore.add(Component.text("§8-인챈트 없음-"));

        return lore;
    }

    public ItemStack build() {
        if (meta != null) {
            meta.lore(buildLore(plugin));
            item.setItemMeta(meta);
        }
        return item;
    }
}
