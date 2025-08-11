package org.kimgooner.tycoon.global.gui.job.mining.heart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.mining.HeartDAO;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;

import java.util.List;
import java.util.Map;

public class CaveHeartEventHandler implements Listener {
    private final HeartDAO heartDAO;
    private final JavaPlugin plugin;
    public CaveHeartEventHandler(JavaPlugin plugin, HeartDAO heartDAO) {
        this.heartDAO = heartDAO;
        this.plugin = plugin;
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

    private final Map<Integer, Integer> LOCATIONS_STAT = Map.ofEntries(
            Map.entry(40, 1),
            Map.entry(29, 2),
            Map.entry(30, 3),
            Map.entry(31, 4),
            Map.entry(32, 5),
            Map.entry(33, 6),
            Map.entry(20, 7),
            Map.entry(22, 8),
            Map.entry(24, 9),
            Map.entry(11, 10),
            Map.entry(12, 11),
            Map.entry(13, 12),
            Map.entry(14, 13),
            Map.entry(15, 14),
            Map.entry(2, 15),
            Map.entry(4, 16),
            Map.entry(6, 17)
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
        Integer target = LOCATIONS_STAT.get(node);
        if(target == null) {
            return false;
        }

        List<Integer> connected = NODE_CONNECTIONS.get(target);
        if(connected == null || connected.isEmpty()) return true;

        for(Integer n : connected){
            if(heartDAO.getLevel(player, n) >= 1) return true;
        }
        return false;
    }

    public void reloadSlot(Inventory gui, Player player, Integer slot) {
        Integer target = LOCATIONS_STAT.get(slot);

        ItemStack baseItem = new ItemStack(Material.COAL);
        Integer level = heartDAO.getLevel(player, target);
        if(level > 0){
            baseItem = new ItemStack(Material.EMERALD, level);
        }

        ItemStack stat = new ItemBuilder(baseItem)
                .displayName(Component.text(STAT_NAMES.get(target)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .build();

        gui.setItem(STATS_LOCATIONS.get(target), stat);
    }

    @EventHandler
    public void onLevelUp(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Integer slot = event.getSlot();

        if(PlainTextComponentSerializer.plainText().serialize(event.getView().title()).equals("동굴의 심장 - 1")){
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return; // 클릭한 아이템 없음
            }

            if (clickedItem.getType() == Material.COAL || clickedItem.getType() == Material.EMERALD) {
                if(!isConnected(player, slot)){
                    player.sendMessage("연결된 노드 x");
                    return;
                }
                heartDAO.addLevel(player, LOCATIONS_STAT.get(slot));
                reloadSlot(event.getClickedInventory(), player, slot);
            }
        }
    }
}
