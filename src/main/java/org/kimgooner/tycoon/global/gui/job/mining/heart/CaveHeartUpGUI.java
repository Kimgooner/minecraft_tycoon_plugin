package org.kimgooner.tycoon.global.gui.job.mining.heart;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.io.InputStream;
import java.util.List;

public class CaveHeartUpGUI {
    private final JavaPlugin plugin;
    private final GlobalGUIController globalGuiController;

    private ChestGui caveHeartUpGUI;

    public CaveHeartUpGUI(JavaPlugin plugin, GlobalGUIController globalGuiController) {
        this.plugin = plugin;
        this.globalGuiController = globalGuiController;

        InputStream xmlStream = plugin.getResource("gui/npc/mining-caveheart-2.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        caveHeartUpGUI = ChestGui.load(this, xmlStream);
        caveHeartUpGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    private final List<Integer> TIER_SLOTS = List.of(
            36, 27, 18, 9, 0
    );

    private final List<Integer> TOKEN_COUNTS = List.of(
            2, 2, 3, 3, 3
    );

    private final List<Integer> STAT_LOCATIONS = List.of(
            38, 40, 42,
            29, 30, 31, 32, 33,
            20, 22, 24,
            11, 12, 13, 14, 15,
            2, 4, 6
    );

    private final List<String> STAT_NAMES = List.of(
            "채광 이벤트 보너스: 속도", "동굴의 심장 코어", "채광 이벤트 보너스: 행운",
            "하위 광석의 가루 기본 값 증가", "풍부한 광물 탐사 확률 증가 II", "연쇄 파괴 증가 II", "광물 탐사 확률 증가 II", "상위 광석의 가루 기본 값 증가",
            "채광 속도 II", "가루 획득량 증가", "채광 행운 II",
            "채광 이벤트 보너스: 상자", "순수 증가 II", "움브랄나이트 탐사", "행운 증가 II", "채광 이벤트 보너스: 가루",
            "광석 변이", "모든 채광 확률 배율", "동굴의 축복"
    );

    public Material getItem(Integer level, Integer raw_cap) {
        Integer cap = raw_cap + 6;
        if (level >= cap) return Material.GREEN_STAINED_GLASS_PANE;
        else if (level == cap-1) return Material.YELLOW_STAINED_GLASS_PANE;
        else return Material.RED_STAINED_GLASS_PANE;
    }

    public String getString(Integer level, Integer cap) {
        if (level >= cap) return "도달한 레벨입니다.";
        else return "아직 도달하지 못한 레벨입니다.";
    }

    public String getLevel(Integer level) {
        return switch (level) {
            case 1 -> "V";
            case 2 -> "VI";
            case 3 -> "VII";
            case 4 -> "VIII";
            case 5 -> "IX";
            default -> "";
        };
    }

    public String getToken(Integer level) {
        return " +" + TOKEN_COUNTS.get(level-1) + " 심장 해방의 열쇠";
    }

    public void open(Player player) {
        Integer caveLevel = 3;
        ChestGui caveGUI = caveHeartUpGUI.copy();
        caveGUI.show(player);

        for(int i = 0; i < STAT_LOCATIONS.size(); i++) {
            ItemStack stat = new ItemBuilder(Material.COAL)
                    .displayName(Component.text(STAT_NAMES.get(i)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .build();

            caveGUI.getInventory().setItem(STAT_LOCATIONS.get(i), stat);
        }

        ItemStack core = new ItemBuilder(Material.GILDED_BLACKSTONE)
                .displayName(Component.text(player.getName() + "의 동굴의 심장").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("강력한 힘이 깃든 동굴의 심장입니다.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(""))
                .addLore(Component.text("현재 레벨: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(""))
                .addLore(Component.text("보유 중인 자원: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 해방의 열쇠: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 하위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 상위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .build();

        caveGUI.getInventory().setItem(49, core);

        ItemStack reset_button = new ItemBuilder(Material.FIRE_CHARGE)
                .displayName(Component.text("동굴의 심장 리셋").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("동굴의 심장에 사용한 열쇠와 가루를").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("모두 돌려 받습니다.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("아래 자원을 돌려 받습니다:").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 해방의 열쇠: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 하위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 상위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("클릭하여 리셋!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .build();

        caveGUI.getInventory().setItem(51, reset_button);

        for(int i = 1; i <= 5; i++) {
            ItemStack tier = new ItemBuilder(getItem(caveLevel, i))
                    .hideAttributeModifiers()
                    .displayName(Component.text("동굴의 심장 ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(getLevel(i)).color(ItemGlowUtil.getDisplayColor(i+3)).decoration(TextDecoration.ITALIC, false))
                    )
                    .addLore(Component.text(getToken(i)).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                    .addLore(Component.text(" "))
                    .addLore(Component.text(getString(caveLevel, i)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .build();

            caveGUI.getInventory().setItem(TIER_SLOTS.get(i-1), tier);
        }
    }

    public void toPrevPage(InventoryClickEvent event) {
        globalGuiController.openCaveHeart((Player) event.getWhoClicked());
    }

    public void toClose(InventoryClickEvent event) {
        globalGuiController.closeInventory(event);
    }
}
