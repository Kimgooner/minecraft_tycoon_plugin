package org.kimgooner.tycoon.global.gui.job.mining.heart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.global.global.SoundUtil;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;

import java.util.List;
import java.util.Objects;

public class CaveHeartUpEventHandler implements Listener {
    private final JavaPlugin plugin;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;
    private final CaveHeartUtil util;
    private final SoundUtil soundUtil = new SoundUtil();

    public CaveHeartUpEventHandler(JavaPlugin plugin, GlobalController globalController) {
        this.plugin = plugin;
        this.heartDAO = globalController.getGlobalDaoController().getHeartDAO();
        this.heartInfoDAO = globalController.getGlobalDaoController().getHeartInfoDAO();
        this.util = new CaveHeartUtil(heartDAO, plugin);
    }

    public void reloadSpecificSlot(Inventory gui, Player player, Integer slot) {
        ItemStack buffer = new ItemBuilder(Material.GREEN_CONCRETE)
                .displayName(Component.text("§a업그레이드 완료!"))
                .build();
        gui.setItem(slot, buffer);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Integer i = util.LOCATIONS_UP.get(slot);
            Integer level = heartDAO.getLevel(player, i);
            ItemStack baseItem = new ItemStack(Material.COAL);
            if (!Objects.equals(level, util.STAT_DATA.get(i).cap())) {
                if (level > 0) baseItem = new ItemStack(Material.EMERALD, level);
                List<String> moreLore = util.STAT_DESCRIPTION.get(i).apply(level);
                ItemBuilder builder = new ItemBuilder(baseItem)
                        .displayName(Component.text(util.STAT_NAMES.get(i)).color(util.getColor(level)).decoration(TextDecoration.ITALIC, false))
                        .addLore(Component.text("§f레벨: %d/§8%d".formatted(level, util.STAT_DATA.get(i).cap())))
                        .addLore(Component.text(""))
                        .addLore(Component.text("§f효과:"));

                for (String s : moreLore) {
                    builder.addLore(Component.text(s));
                }

                ItemStack stat = builder
                        .addLore(Component.text(" "))
                        .addLore(Component.text("§f비용:"))
                        .addLore(Component.text(util.getCost(i, level)))
                        .build();

                gui.setItem(util.STATS_LOCATIONS.get(i), stat);
            } else {
                baseItem = new ItemStack(Material.EMERALD_BLOCK, level);
                List<String> moreLore = util.STAT_DESCRIPTION_MAX.get(i).apply(level);
                ItemBuilder builder = new ItemBuilder(baseItem)
                        .displayName(Component.text(util.STAT_NAMES.get(i)).color(util.getColor(level)).decoration(TextDecoration.ITALIC, false))
                        .addLore(Component.text("§f레벨: %d/§8%d".formatted(level, util.STAT_DATA.get(i).cap())))
                        .addLore(Component.text(""))
                        .addLore(Component.text("§f효과:"));

                for (String s : moreLore) {
                    builder.addLore(Component.text(s));
                }

                ItemStack stat = builder
                        .addLore(Component.text(" "))
                        .addLore(Component.text("§f비용:"))
                        .addLore(Component.text("§a최대 레벨입니다!"))
                        .build();

                gui.setItem(util.STATS_LOCATIONS.get(i), stat);
            }
        }, 5L);
    }

    public void reloadHeartAndReset(Inventory gui, Player player) {
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

        gui.setItem(49, core);

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

        gui.setItem(51, reset_button);
    }

    @EventHandler
    public void onLevelUp(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory gui = event.getInventory();
        Integer slot = event.getSlot();

        if(PlainTextComponentSerializer.plainText().serialize(event.getView().title()).equals("동굴의 심장 - 1")){
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return; // 클릭한 아이템 없음
            }

            if (clickedItem.getType() == Material.COAL || clickedItem.getType() == Material.EMERALD) {
                Integer id = util.LOCATIONS_UP.get(slot);
                Integer level = heartDAO.getLevel(player, id);

                if(util.STAT_DATA.get(id).level() > heartInfoDAO.getLevel(player)){
                    util.notEnoughLevel(gui, player, clickedItem, slot);
                    return;
                }

                if(!util.isConnected(player, id)){
                    util.notConnected(gui, player, clickedItem, slot);
                    return;
                }

                if(level == 0) {
                    if(heartInfoDAO.getHeartKey(player) < 1){
                        util.notEnoughKey(gui, player, clickedItem, slot);
                        return;
                    }
                    else{
                        heartInfoDAO.removeHeartKey(player, 1);
                    }
                }
                else {
                    if (util.STAT_DATA.get(id).type() == 1) {
                        if (heartInfoDAO.getLowPowder(player) < util.STAT_DATA.get(id).usage().apply(level + 1)) {
                            util.notEnoughPowder(gui, player, clickedItem, slot, 1);
                            return;
                        }
                        heartInfoDAO.removeLowPowder(player, util.STAT_DATA.get(id).usage().apply(level + 1));
                    } else {
                        if (heartInfoDAO.getHighPowder(player) < util.STAT_DATA.get(id).usage().apply(level + 1)) {
                            util.notEnoughPowder(gui, player, clickedItem, slot, 2);
                            return;
                        }
                        heartInfoDAO.removeHighPowder(player, util.STAT_DATA.get(id).usage().apply(level + 1));
                    }
                }
                heartDAO.addLevel(player, util.LOCATIONS.get(slot));
                soundUtil.playPositiveSound(player);
                reloadSpecificSlot(gui, player, slot);
                reloadHeartAndReset(gui, player);
            }
        }
    }

}
