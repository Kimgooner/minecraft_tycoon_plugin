package org.kimgooner.tycoon.job.mining;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.template.ItemTemplate;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MineEventHandler implements Listener {
    private final JavaPlugin plugin;

    public MineEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleportTo(Location location, Player player) {
        player.teleport(location);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
        }, 1L);
    }

    public void teleportToMineHub(Player player) {
        player.teleport(hubLocation);
        player.sendMessage(Component.text("[시스템] - §f§l§n광산 허브§f로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
        }, 1L);
    }

    // 현재 각 플레이어가 속한 리전 ID 저장
    private final Map<UUID, Set<String>> playerRegions = new HashMap<>();

    // 좌표 지정
    World world = Bukkit.getWorld("world");
    Location hubLocation = new Location(world, 0.5, 65, 434.5, 180, 0);
    Location zone1Location = new Location(world, 0.5, 66, 466.5, 0, 0);
    Location zone2Location = new Location(world, 9.5, 49, 505.5, 90, 0);
    Location zone3Location = new Location(world, -24.5, 32, 518.5, 180, 0);
    Location zone4Location = new Location(world, -38.5, 15, 466.5, 0, 0);

    private final Map<String, Consumer<Player>> regionActions = Map.of(
            "mine_enter_zone", this::open,
            "mine1_leave_zone", this::teleportToMineHub,
            "mine2_leave_zone", this::teleportToMineHub,
            "mine3_leave_zone", this::teleportToMineHub,
            "mine4_leave_zone", this::teleportToMineHub,
            "region3", player -> {
                player.sendMessage("§c[Region3] 입장! 보스가 깨어납니다...");
                // 예: 보스 소환 코드
            }
    );

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 블럭 단위 위치 변화가 없으면 무시
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
        if (regionManager == null) return;

        Location location = player.getLocation();
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        Set<String> currentRegionIds = regionSet.getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toSet());

        UUID uuid = player.getUniqueId();
        Set<String> previousRegionIds = playerRegions.getOrDefault(uuid, new HashSet<>());

        // 새롭게 들어간 리전들만 탐지
        Set<String> newlyEntered = new HashSet<>(currentRegionIds);
        newlyEntered.removeAll(previousRegionIds);

        for (String regionId : newlyEntered) {
            if (regionActions.containsKey(regionId)) {
                regionActions.get(regionId).accept(player);
            }
        }
        // 현재 리전 상태 갱신
        playerRegions.put(uuid, currentRegionIds);
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27,
                Component.text("광산 입장").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
        );

        ItemStack mine1 = new ItemBuilder(Material.CALCITE)
                .displayName(Component.text("버려진 채석장").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true))
                .addLore(Component.text("오래 전에는 활발하게 광석 채굴이 이워지던 곳이지만,").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text("이제는 버려져 사용되지 않는다.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text(" "))
                .addLore(Component.text("최소 ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("빛 ✦").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" 요구량: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("채광 속도 배율: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("x1.0").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" "))
                .addLore(Component.text("아래 광물을 발견 가능합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("돌").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("석탄").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("구리").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("철").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("금").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("레드스톤").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("청금석").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("에메랄드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("다이아몬드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(10, mine1);

        ItemStack mine2 = new ItemBuilder(Material.POLISHED_ANDESITE)
                .displayName(Component.text("깊은 동굴").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true))
                .addLore(Component.text("높은 압력으로 인해 지반이 아주 단단한 장소.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text("대신 그만큼 희귀한 광물이 많이 분포되어 있다.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text(" "))
                .addLore(Component.text("최소 ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("빛 ✦").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" 요구량: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("채광 속도 배율: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("x0.75").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" "))
                .addLore(Component.text("아래 광물을 발견 가능합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("돌").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("석탄").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("구리").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("철").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("금").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("레드스톤").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("청금석").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("에메랄드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("다이아몬드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("미스릴").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("티타늄").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("자수정").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("아쿠아마린").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(12, mine2);

        ItemStack mine3 = new ItemBuilder(Material.DEEPSLATE)
                .displayName(Component.text("미탐사 구역").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true))
                .addLore(Component.text("적절한 장비 없이는 도달하자 마자 압력에 짓이겨지는 장소.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text("위험을 감수할 만한 물건이 있을까?").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text(" "))
                .addLore(Component.text("최소 ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("빛 ✦").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" 요구량: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("채광 속도 배율: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("x0.50").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" "))
                .addLore(Component.text("아래 광물을 발견 가능합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("돌").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("석탄").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("구리").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("철").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("금").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("레드스톤").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("청금석").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("에메랄드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("다이아몬드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("미스릴").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("티타늄").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("자수정").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("아쿠아마린").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("오팔").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("오닉스").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("폴룩사이트").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("탄탈라이트").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(14, mine3);

        ItemStack mine4 = new ItemBuilder(Material.DEEPSLATE_TILES)
                .displayName(Component.text("종착점").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true))
                .addLore(Component.text("...").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC ,false))
                .addLore(Component.text(" "))
                .addLore(Component.text("최소 ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("빛 ✦").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" 요구량: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("채광 속도 배율: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("x0.25").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text(" "))
                .addLore(Component.text("아래 광물을 발견 가능합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("돌").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("석탄").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("구리").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("철").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("금").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("레드스톤").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("청금석").color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("에메랄드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("다이아몬드").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("미스릴").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("티타늄").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("자수정").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("아쿠아마린").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("오팔").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("오닉스").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("- ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("폴룩사이트").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("탄탈라이트").color(NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(16, mine4);

        ItemStack main = new ItemBuilder(Material.GRASS_BLOCK)
                .displayName(Component.text("광산 선택").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false))
                .build();

        gui.setItem(4, main);
        gui.setItem(22, ItemTemplate.getBackward());

        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.0f);
        player.openInventory(gui);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(!event.getView().title().equals(Component.text("광산 입장").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch (event.getSlot()){
            case 10 -> {
                teleportTo(zone1Location, player);
                player.sendMessage(Component.text("[시스템] - §f§l§n버려진 채석장§f으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            }
            case 12 -> {
                teleportTo(zone2Location, player);
                player.sendMessage(Component.text("[시스템] - §f§l§n깊은 동굴§f로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            }
            case 14 -> {
                teleportTo(zone3Location, player);
                player.sendMessage(Component.text("[시스템] - §f§l§n미탐사 구역§f으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            }
            case 16 -> {
                teleportTo(zone4Location, player);
                player.sendMessage(Component.text("[시스템] - §f§l§n종착점§f으로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            }
            case 22 -> teleportTo(hubLocation, player);
        }
    }
}
