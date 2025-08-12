package org.kimgooner.tycoon.global.gui.job.mining.heart;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CaveHeartUtil {
    private final HeartDAO heartDAO;
    private final JavaPlugin plugin;
    public CaveHeartUtil(HeartDAO heartDAO, JavaPlugin plugin) {
        this.heartDAO = heartDAO;
        this.plugin = plugin;
    }

    static Function<Integer, Integer> calcPow(double exponent) {
        return (x) -> (int) Math.floor(Math.pow(x + 1, exponent));
    }

    public final Map<Integer, Integer> LOCATIONS = Map.ofEntries(
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

    public final Map<Integer, Integer> LOCATIONS_UP = Map.ofEntries(
            Map.entry(38, 15),
            Map.entry(40, 16),
            Map.entry(42, 17),

            Map.entry(29, 18),
            Map.entry(30, 19),
            Map.entry(31, 20),
            Map.entry(32, 21),
            Map.entry(33, 22),

            Map.entry(20, 23),
            Map.entry(22, 24),
            Map.entry(24, 25),

            Map.entry(11, 26),
            Map.entry(12, 27),
            Map.entry(13, 28),
            Map.entry(14, 29),
            Map.entry(15, 30),

            Map.entry(2, 31),
            Map.entry(4, 32),
            Map.entry(6, 33)
    );


    public final Map<Integer, Integer> STATS_LOCATIONS = Map.ofEntries(
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

    public final Map<Integer, Integer> STATS_LOCATIONS_UP = Map.ofEntries(
            Map.entry(15, 38),
            Map.entry(16, 40),
            Map.entry(17, 42),

            Map.entry(18, 29),
            Map.entry(19, 30),
            Map.entry(20, 31),
            Map.entry(21, 32),
            Map.entry(22, 33),

            Map.entry(23, 20),
            Map.entry(24, 22),
            Map.entry(25, 24),

            Map.entry(26, 11),
            Map.entry(27, 12),
            Map.entry(28, 13),
            Map.entry(29, 14),
            Map.entry(30, 15),

            Map.entry(31, 2),
            Map.entry(32, 4),
            Map.entry(33, 6)
    );

    public final Map<Integer, String> STAT_NAMES = Map.ofEntries(
            Map.entry(1, "채광 속도 증가 I"),

            Map.entry(2, "연속적인 채광: 행운"),
            Map.entry(3, "광물 탐사 확률 증가 I"),
            Map.entry(4, "채광 행운 증가 I"),
            Map.entry(5, "풍부한 광물 탐사 확률 증가 I"),
            Map.entry(6, "연속적인 채광: 속도"),

            Map.entry(7, "보물 상자 발견 확률 증가"),
            Map.entry(8, "연쇄 파괴 증가 I"),
            Map.entry(9, "상위 보물 상자 드랍 확률 증가"),

            Map.entry(10, "광물 재생 속도 감소"),
            Map.entry(11, "빛 증가 I"),
            Map.entry(12, "균열 광물 탐사"),
            Map.entry(13, "순수 증가 I"),
            Map.entry(14, "채광 경험치 획득량 증가"),

            Map.entry(15, "채광 이벤트 보너스: 속도"),
            Map.entry(16, "숙련된 광부"),
            Map.entry(17, "채광 이벤트 보너스: 행운"),

            Map.entry(18, "하위 광석의 가루 기본 값 증가"),
            Map.entry(19, "풍부한 광물 탐사 확률 증가 II"),
            Map.entry(20, "연쇄 파괴 증가 II"),
            Map.entry(21, "광물 탐사 확률 증가 II"),
            Map.entry(22, "상위 광석의 가루 기본 값 증가"),

            Map.entry(23, "채광 속도 II"),
            Map.entry(24, "가루 획득량 증가"),
            Map.entry(25, "채광 행운 II"),

            Map.entry(26, "채광 이벤트 보너스: 상자"),
            Map.entry(27, "순수 증가 II"),
            Map.entry(28, "움브랄나이트 탐사"),
            Map.entry(29, "행운 증가 II"),
            Map.entry(30, "채광 이벤트 보너스: 가루"),

            Map.entry(31, "광석 변이"),
            Map.entry(32, "모든 채광 확률 배율"),
            Map.entry(33, "동굴의 축복")
    );

    public final Map<Integer, List<Integer>> NODE_CONNECTIONS = Map.ofEntries(
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
            Map.entry(17, List.of(14, 22)),
            Map.entry(18, List.of(15, 19, 23)),
            Map.entry(19, List.of(18, 20)),
            Map.entry(20, List.of(16, 19, 21, 24)),
            Map.entry(21, List.of(20, 22)),
            Map.entry(22, List.of(17, 21, 25)),
            Map.entry(23, List.of(18, 26)),
            Map.entry(24, List.of(20, 28)),
            Map.entry(25, List.of(22, 30)),
            Map.entry(26, List.of(23, 27, 31)),
            Map.entry(27, List.of(26, 28)),
            Map.entry(28, List.of(24, 27, 29, 32)),
            Map.entry(29, List.of(28, 30)),
            Map.entry(30, List.of(25, 29, 33)),
            Map.entry(31, List.of(26)),
            Map.entry(32, List.of(28)),
            Map.entry(33, List.of(30))
    );

    public record upgradeDate(Integer level, Function<Integer, Integer> usage, Integer type, Integer cap) {}

    public final Map<Integer, upgradeDate> STAT_DATA = Map.ofEntries(
            Map.entry(1, new upgradeDate(1, CaveHeartUtil.calcPow(3), 1, 50)),// 50

            Map.entry(2, new upgradeDate(2, (x) -> 0, 1,  1)),// 1
            Map.entry(3, new upgradeDate(2, CaveHeartUtil.calcPow(3.5), 1,  30)), // 30
            Map.entry(4, new upgradeDate(2, CaveHeartUtil.calcPow(3.05), 1,   50)), // 50
            Map.entry(5, new upgradeDate(2, CaveHeartUtil.calcPow(3), 1,   50)), // 50
            Map.entry(6, new upgradeDate(2, (x) -> 0, 1,  1)), // 1

            Map.entry(7, new upgradeDate(3, CaveHeartUtil.calcPow(3), 1,  50)), // 50
            Map.entry(8, new upgradeDate(3, CaveHeartUtil.calcPow(2.6), 1,  100)), // 100
            Map.entry(9, new upgradeDate(3, CaveHeartUtil.calcPow(3), 1,  50)), // 50

            Map.entry(10, new upgradeDate(4, CaveHeartUtil.calcPow(3.1), 1,  30)), // 100
            Map.entry(11, new upgradeDate(4, CaveHeartUtil.calcPow(4), 1,  20)), // 20
            Map.entry(12, new upgradeDate(4, (x) -> 0, 1,1)), // 1
            Map.entry(13, new upgradeDate(4, CaveHeartUtil.calcPow(6), 1,  10)), // 20
            Map.entry(14, new upgradeDate(4, CaveHeartUtil.calcPow(3), 1,  30)), // 100

            Map.entry(15, new upgradeDate(5, (x) -> 0, 2,1)), // 100
            Map.entry(16, new upgradeDate(5, CaveHeartUtil.calcPow(2.4), 2,150)), // 100
            Map.entry(17, new upgradeDate(5, (x) -> 0, 2,1))// 100
    );

    public final Map<Integer, Function<Integer, List<String>>> STAT_DESCRIPTION = Map.ofEntries(
            // 1
            Map.entry(1, (level) -> List.of("§6채광 속도 ⸕ §f: " + "§a+%d §f-> §8+%d".formatted((level)*20, (level+1)*20))),

            // 2
            Map.entry(2, (level) -> List.of("§f블럭을 캘 때마다 §6채광 행운 ☘§f가 §a1§f씩 증가합니다 (최대 §a50§f)",
                    "§f3초 이내에 블럭을 캐지 않을 경우 초기화됩니다.")),
            Map.entry(3, (level) -> List.of("§7광물 §f발견 확률 §f: " + "§a+%d%% §f-> §8+%d%% §f증가".formatted(level, level+1),
                    "§8광산에서 광물 재생 시",
                    "§8광물이 등장할 확률을 증가시킵니다. (기본 50%)"
            )),
            Map.entry(4, (level) -> List.of("§6채광 행운 ☘ §f: " + "§a+%d §f-> §8+%d".formatted((level)*2, (level+1)*2))),
            Map.entry(5, (level) -> List.of("§7풍부한 광물 §f발견 확률 §f: " + "§a%d%% §f-> §8%d%% §f증가".formatted(level, level+1),
                    "§8광산에서 광물 재생의 결과로 광물이 등장했을 때,",
                    "§8풍부한 광물이 등장할 확률을 증가시킵니다. (기본 20%)"
            )),
            Map.entry(6, (level) -> List.of("§f블럭을 캘 때마다 §6채광 속도 ⸕§f가 §a1§f씩 증가합니다 (최대 §a200§f)",
                    "§f3초 이내에 블럭을 캐지 않을 경우 초기화됩니다.")),

            // 3
            Map.entry(7, (level) -> List.of("§e보물 상자 §f발견 확률 §f: " + "§a%d%% §f-> §8%d%% §f증가".formatted(level, level+1),
                    "§8광산에서 광물을 캘 때",
                    "§8보물 상자를 획득할 확률을 증가시킵니다.",
                    " ",
                    "§8기본 등장 확률:",
                    "§8버려진 채석장 - 0.14%",
                    "§8깊은 동굴 - 0.18%",
                    "§8미탐사 구역 - 0.2%",
                    "§8종착점 - 0.27%"
            )),
            Map.entry(8, (level) -> List.of("§e연쇄 파괴 ▚ §f: " + "§a+%d §f-> §8+%d".formatted((level)*3, (level+1)*3))),
            Map.entry(9, (level) -> List.of("§e상위 보물 상자 §f발견 확률 §f: " + "§a+%d%% §f-> §8+%d%% §f증가".formatted(level, level+1),
                    "§8광산에서 보물 상자를 획득할 때",
                    "§8상위 보물 상자를 획득할 확률을 증가시킵니다. (기본 20%)"
            )),

            //4
            Map.entry(10, (level) -> List.of("§7광물 재생 속도 §f감소 §f: " + "§a-%.1f초 §f-> §8-%.1f초".formatted(level*(0.05), (level+1)*(0.05)),
                    "§8광산에서 채광된 블럭(베드락)이",
                    "§8다시 돌이나 광물로 돌아오는 시간을 감소시킵니다. (기본 3초)"
            )),
            Map.entry(11, (level) -> List.of("§e빛 ✦ §f: " + "§a+%d §f-> §8+%d".formatted((level)*10, (level+1)*10))),
            Map.entry(12, (level) -> List.of("§5균열 광물§f을 발견할 수 있게 됩니다.",
                    "§8균열 광물은 채굴 시 다량의 광석의 가루를 획득할 수 있습니다.",
                    "§8하위 균열 광물과 상위 균열 광물로 나뉘며, 각각 해당하는",
                    "§8광석의 가루를 획득 가능합니다.",
                    "",
                    "§8균열 광물 발견 확률 - 광물 발견 시 1%",
                    "§8하위 균열 광물 - 균열 광물 발견 시 60%",
                    "§8상위 균열 광물 - 균열 광물 발견 시 40%"
            )),
            Map.entry(13, (level) -> List.of("§5순수 ✧ §f: " + "§a+%d §f-> §8+%d".formatted((level), (level+1)))),
            Map.entry(14, (level) -> List.of("§3채광 숙련 §f: " + "§a+%.1f §f-> §8+%.1f".formatted(level*(0.5), (level+1)*(0.5)))),

            //5
            Map.entry(15, (level) -> List.of("§b채광 이벤트§f가 진행 중인 경우,",
                    "§6채광 속도 ⸕§f가 §a500 §f증가합니다.")),
            Map.entry(16, (level) -> List.of("§6채광 속도 ⸕ §f: " + "§a+%d §f-> §8+%d".formatted((level)*5, (level+1)*5),
                    "§6채광 행운 ☘ §f: " + "§a+%d §f-> §8+%d".formatted((level), (level+1))
            )),
            Map.entry(17, (level) -> List.of("§b채광 이벤트§f가 진행 중인 경우,",
                    "§6채광 행운 ☘§f이 §a100 §f증가합니다."))
    );

    public final List<Integer> TIER_SLOTS = List.of(
            36, 27, 18, 9, 0
    );

    public final List<Integer> TOKEN_COUNTS = List.of(
            1, 2, 2, 2, 2, 2, 3, 3, 3
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
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            default -> "";
        };
    }

    public String getToken(Integer level) {
        return " +" + TOKEN_COUNTS.get(level-1) + " 심장 해방의 열쇠";
    }

    public NamedTextColor getColor(Integer level) {
        if(level == 0) return NamedTextColor.RED;
        else return NamedTextColor.GREEN;
    }

    public String getCost(Integer index, Integer level) {
        if(level == 0) return "§5심장 해방의 열쇠 §f1개";
        else {
            if(STAT_DATA.get(index).type == 1) return "§a하위 §f광석의 가루: §a%,d".formatted(STAT_DATA.get(index).usage().apply(level));
            else return "§d상위 §f광석의 가루: §a%,d".formatted(STAT_DATA.get(index).usage().apply(level));
        }
    }

    public boolean isConnected(Player player, Integer target) {
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


    public void notConnected(Inventory gui, Player player, ItemStack prev, Integer slot) {
        ItemStack item = new ItemBuilder(Material.RED_CONCRETE)
                .hideAttributeModifiers()
                .displayName(Component.text("§f다른 노드와 연결되지 않았습니다!"))
                .build();
        gui.setItem(slot, item);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> gui.setItem(slot, prev), 10L);
    }

    public void notEnoughLevel(Inventory gui, Player player, ItemStack prev, Integer slot) {
        ItemStack item = new ItemBuilder(Material.RED_CONCRETE)
                .hideAttributeModifiers()
                .displayName(Component.text("§f달성하지 못한 등급입니다!"))
                .build();
        gui.setItem(slot, item);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> gui.setItem(slot, prev), 10L);
    }

    public void notEnoughKey(Inventory gui, Player player, ItemStack prev, Integer slot) {
        ItemStack item = new ItemBuilder(Material.RED_CONCRETE)
                .hideAttributeModifiers()
                .displayName(Component.text("§5해방의 열쇠§f가 부족합니다!"))
                .build();
        gui.setItem(slot, item);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> gui.setItem(slot, prev), 10L);
    }

    public void notEnoughPowder(Inventory gui, Player player, ItemStack prev, Integer slot, Integer type) {
        ItemStack item;
        if(type == 1) {
            item = new ItemBuilder(Material.RED_CONCRETE)
                    .hideAttributeModifiers()
                    .displayName(Component.text("§a하위 §f광석의 가루가 부족합니다!"))
                    .build();
        }
        else{
            item = new ItemBuilder(Material.RED_CONCRETE)
                    .hideAttributeModifiers()
                    .displayName(Component.text("§d상위 §f광석의 가루가 부족합니다!"))
                    .build();
        }
        gui.setItem(slot, item);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> gui.setItem(slot, prev), 10L);
    }
}
