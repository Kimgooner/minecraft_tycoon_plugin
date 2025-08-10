package org.kimgooner.tycoon.global.gui.job.mining;

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

public class CaveHeartGUI {
    private final JavaPlugin plugin;
    private final GlobalGUIController globalGuiController;

    private ChestGui caveHeartGUI;

    public CaveHeartGUI(JavaPlugin plugin, GlobalGUIController globalGuiController) {
        this.plugin = plugin;
        this.globalGuiController = globalGuiController;

        InputStream xmlStream = plugin.getResource("gui/npc/mining-caveheart.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        caveHeartGUI = ChestGui.load(this, xmlStream);
        caveHeartGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    private final List<Integer> TIER_SLOTS = List.of(
            36, 27, 18, 9, 0
    );

    private final List<Integer> TOKEN_COUNTS = List.of(
            1, 2, 2, 2, 2
    );

    private final List<Integer> STAT_LOCATIONS = List.of(
            40,
            29, 30, 31, 32, 33,
            20, 22, 24,
            11, 12, 13, 14, 15,
            2, 4, 6
    );

    private final List<String> STAT_NAMES = List.of(
            "채광 속도 증가 I",
            "연속적인 채광: 행운", "광물 탐사 확률 증가 I", "채광 행운 증가 I", "풍부한 광물 탐사 확률 증가 I", "연속적인 채광: 속도",
            "보물 상자 발견 확률 증가", "연쇄 파괴 증가 I", "상위 보물 상자 드랍 확률 증가",
            "광물 재생 속도 증가", "빛 증가 I", "균열 광물 탐사", "순수 증가 I", "채광 경험치 획득량 증가",
            "채광 이벤트 보너스: 속도", "동굴의 심장 코어", "채광 이벤트 보너스: 행운"
    );

    public Material getItem(Integer level, Integer cap) {
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
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> "";
        };
    }

    public String getToken(Integer level) {
        return " +" + TOKEN_COUNTS.get(level-1) + " 심장 해방의 열쇠";
    }

    public void open(Player player) {
        Integer caveLevel = 3;
        ChestGui caveGUI = caveHeartGUI.copy();
        caveGUI.show(player);

        for(int i = 0; i < STAT_LOCATIONS.size(); i++) {
            ItemStack stat = new ItemBuilder(Material.COAL)
                    .displayName(Component.text(STAT_NAMES.get(i)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .build();

            caveGUI.getInventory().setItem(STAT_LOCATIONS.get(i), stat);
        }

        ItemStack core = new ItemBuilder(Material.GILDED_BLACKSTONE)
                .displayName(Component.text(player.getName() + "의 동굴의 심장").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
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
                            .append(Component.text(getLevel(i)).color(ItemGlowUtil.getDisplayColor(i-1)).decoration(TextDecoration.ITALIC, false))
                    )
                    .addLore(Component.text(getToken(i)).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                    .addLore(Component.text(" "))
                    .addLore(Component.text(getString(caveLevel, i)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .build();

            caveGUI.getInventory().setItem(TIER_SLOTS.get(i-1), tier);
        }
    }

    public void toNextPage(InventoryClickEvent event) {
        globalGuiController.openCaveHeartUp((Player) event.getWhoClicked());
    }

    public void toClose(InventoryClickEvent event) {
        globalGuiController.closeInventory(event);
    }
}
