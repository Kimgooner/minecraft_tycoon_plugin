package org.kimgooner.tycoon.global.gui.job.mining.heart;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.mining.HeartInfoDAO;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class CaveHeartGUI {
    private final JavaPlugin plugin;
    private final GlobalGUIController globalGuiController;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;

    private ChestGui caveHeartGUI;

    public CaveHeartGUI(JavaPlugin plugin, HeartDAO heartDAO, HeartInfoDAO heartInfoDAO, GlobalGUIController globalGuiController) {
        this.plugin = plugin;
        this.globalGuiController = globalGuiController;
        this.heartDAO = heartDAO;
        this.heartInfoDAO = heartInfoDAO;

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

    private final Map<Integer, Integer> STATS_LOCATIONS = Map.ofEntries(
            Map.entry(1, 40),
            Map.entry(2, 29),
            Map.entry(3, 30),
            Map.entry(4, 31),
            Map.entry(5, 32),
            Map.entry(6, 33),
            Map.entry(7, 20),
            Map.entry(8, 22),
            Map.entry(9, 24),
            Map.entry(10, 11),
            Map.entry(11, 12),
            Map.entry(12, 13),
            Map.entry(13, 14),
            Map.entry(14, 15),
            Map.entry(15, 2),
            Map.entry(16, 4),
            Map.entry(17, 6)
    );

    private final Map<Integer, String> STAT_NAMES = Map.ofEntries(
            Map.entry(1, "채광 속도 증가 I"),
            Map.entry(2, "연속적인 채광: 행운"),
            Map.entry(3, "광물 탐사 확률 증가 I"),
            Map.entry(4, "채광 행운 증가 I"),
            Map.entry(5, "풍부한 광물 탐사 확률 증가 I"),
            Map.entry(6, "연속적인 채광: 속도"),
            Map.entry(7, "보물 상자 발견 확률 증가"),
            Map.entry(8, "연쇄 파괴 증가 I"),
            Map.entry(9, "상위 보물 상자 드랍 확률 증가"),
            Map.entry(10, "광물 재생 속도 증가"),
            Map.entry(11, "빛 증가 I"),
            Map.entry(12, "균열 광물 탐사"),
            Map.entry(13, "순수 증가 I"),
            Map.entry(14, "채광 경험치 획득량 증가"),
            Map.entry(15, "채광 이벤트 보너스: 속도"),
            Map.entry(16, "동굴의 심장 코어"),
            Map.entry(17, "채광 이벤트 보너스: 행운")
    );

    private final Map<Integer, List<Integer>> NODE_CONNECTIONS = Map.ofEntries(
            Map.entry(1, List.of()),
            Map.entry(2, List.of(3, 7)),
            Map.entry(3, List.of(2, 4)),
            Map.entry(4, List.of(1, 3, 5, 7)),
            Map.entry(5, List.of(4, 6)),
            Map.entry(6, List.of(5, 9)),
            Map.entry(7, List.of(2, 10)),
            Map.entry(8, List.of(4, 12)),
            Map.entry(9, List.of(6, 14)),
            Map.entry(10, List.of(7, 11, 15)),
            Map.entry(11, List.of(10, 12)),
            Map.entry(12, List.of(8, 11, 13, 16)),
            Map.entry(13, List.of(12, 14)),
            Map.entry(14, List.of(9, 13, 17)),
            Map.entry(15, List.of(10, 18)),
            Map.entry(16, List.of(12, 20)),
            Map.entry(17, List.of(14, 22))
    );

    public boolean isConnected(Player player, Integer node) {
        for(int n : NODE_CONNECTIONS.get(node)){
            if(heartDAO.getLevel(player, n) > 0) return true;
        }
        return false;
    }

    public void reloadSlot(Inventory gui, Player player) {
        for(int i = 1; i <= STATS_LOCATIONS.size(); i++) {
            ItemStack baseItem = new ItemStack(Material.COAL);
            Integer level = heartDAO.getLevel(player, i);
            if(level > 0){
                baseItem = new ItemStack(Material.EMERALD, level);
            }

            ItemStack stat = new ItemBuilder(baseItem)
                    .displayName(Component.text(STAT_NAMES.get(i)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .build();

            gui.setItem(STATS_LOCATIONS.get(i), stat);
        }
    }

    public void open(Player player) {
        Integer caveLevel = 3;
        ChestGui caveGUI = caveHeartGUI.copy();
        caveGUI.show(player);

        ItemStack core = new ItemBuilder(Material.GILDED_BLACKSTONE)
                .displayName(Component.text(player.getName() + "의 동굴의 심장").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("강력한 힘이 깃든 동굴의 심장입니다.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(""))
                .addLore(Component.text("현재 레벨: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(heartInfoDAO.getLevel(player)).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(""))
                .addLore(Component.text("보유 중인 자원: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 해방의 열쇠: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,d", heartInfoDAO.getHeartKey(player))).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" 하위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,d", heartInfoDAO.getLowPowder(player))).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" 상위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,d", heartInfoDAO.getHighPowder(player))).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .build();

        caveGUI.getInventory().setItem(49, core);

        ItemStack reset_button = new ItemBuilder(Material.FIRE_CHARGE)
                .displayName(Component.text("동굴의 심장 리셋").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("동굴의 심장에 사용한 열쇠와 가루를").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("모두 돌려 받습니다.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("아래 자원을 돌려 받습니다:").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" 해방의 열쇠: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,d", heartInfoDAO.getUsedHeartKey(player))).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" 하위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,d", heartInfoDAO.getUsedLowPowder(player))).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" 상위 광석의 가루: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,d", heartInfoDAO.getUsedHighPowder(player))).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)))
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

        reloadSlot(caveGUI.getInventory(), player);
    }

    public void toNextPage(InventoryClickEvent event) {
        globalGuiController.openCaveHeartUp((Player) event.getWhoClicked());
    }

    public void toClose(InventoryClickEvent event) {
        globalGuiController.closeInventory(event);
    }
}
