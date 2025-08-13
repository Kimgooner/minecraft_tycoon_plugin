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
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.global.global.SoundUtil;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.item.global.ItemGlowUtil;

import java.io.InputStream;
import java.util.List;

public class CaveHeartGUI {
    private final GlobalGUIController globalGuiController;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;
    private final CaveHeartUtil util;
    private final SoundUtil soundUtil =  new SoundUtil();

    private ChestGui caveHeartGUI;

    public CaveHeartGUI(JavaPlugin plugin, GlobalGUIController globalGUIController, GlobalController globalController) {
        this.globalGuiController = globalGUIController;

        this.heartDAO = globalController.getGlobalDaoController().getHeartDAO();
        this.heartInfoDAO = globalController.getGlobalDaoController().getHeartInfoDAO();
        this.util = new CaveHeartUtil(heartDAO, plugin);

        InputStream xmlStream = plugin.getResource("gui/npc/mining-caveheart.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        caveHeartGUI = ChestGui.load(this, xmlStream);
        caveHeartGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    public void reloadSlot(Inventory gui, Player player) {
        for(int i = 1; i <= util.STATS_LOCATIONS.size(); i++) {
            ItemStack baseItem = new ItemStack(Material.COAL);
            Integer level = heartDAO.getLevel(player, i);
            if(level > 0){
                baseItem = new ItemStack(Material.EMERALD, level);
            }

            List<String> moreLore = util.STAT_DESCRIPTION.get(i).apply(level);
            ItemBuilder builder = new ItemBuilder(baseItem)
                    .displayName(Component.text(util.STAT_NAMES.get(i)).color(util.getColor(level)).decoration(TextDecoration.ITALIC, false))
                    .addLore(Component.text("§f레벨: %d/§8%d".formatted(level, util.STAT_DATA.get(i).cap())))
                    .addLore(Component.text(""))
                    .addLore(Component.text("§f효과:"));

            for(String s : moreLore){
                builder.addLore(Component.text(s));
            }

            ItemStack stat = builder
                    .addLore(Component.text(" "))
                    .addLore(Component.text("§f비용:"))
                    .addLore(Component.text(util.getCost(i, level)))
                    .build();

            gui.setItem(util.STATS_LOCATIONS.get(i), stat);
        }
    }

    public void open(Player player) {
        Integer caveLevel = heartInfoDAO.getLevel(player);
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
            ItemStack tier = new ItemBuilder(util.getItem(caveLevel, i))
                    .hideAttributeModifiers()
                    .displayName(Component.text("동굴의 심장 ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                            .append(Component.text(util.getLevel(i)).color(ItemGlowUtil.getDisplayColor(i-1)).decoration(TextDecoration.ITALIC, false))
                    )
                    .addLore(Component.text(util.getToken(i)).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                    .addLore(Component.text(" "))
                    .addLore(Component.text(util.getString(caveLevel, i)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                    .build();

            caveGUI.getInventory().setItem(util.TIER_SLOTS.get(i-1), tier);
        }

        reloadSlot(caveGUI.getInventory(), player);
    }

    public void toNextPage(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.openCaveHeartUp(player);
        soundUtil.playGUISound(player);
    }

    public void toClose(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.closeInventory(event);
        soundUtil.playGUISound(player);
    }
}
