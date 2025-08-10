package org.kimgooner.tycoon.global.gui.job.mining;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

import java.io.InputStream;
import java.util.List;

public class MineTeleportGUI {
    private final JavaPlugin plugin;
    private final GlobalGUIController globalGuiController;

    private ChestGui mineTeleportGUI;

    List<String> MINE_NAME = List.of(
            "§f§l§n버려진 채석장",
            "§f§l§n깊은 동굴",
            "§f§l§n미탐사 구역",
            "§f§l§n종착점"
    );

    World world = Bukkit.getWorld("world");
    List<Location> mineZone = List.of(
            new Location(world, 0.5, 66, 466.5, 0, 0),
            new Location(world, 9.5, 49, 505.5, 90, 0),
            new Location(world, -24.5, 32, 518.5, 180, 0),
            new Location(world, -38.5, 15, 466.5, 0, 0)
    );

    public MineTeleportGUI(JavaPlugin plugin, GlobalGUIController globalGuiController) {
        this.plugin = plugin;
        this.globalGuiController = globalGuiController;
        InputStream xmlStream = plugin.getResource("gui/mining/mineTeleport.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        mineTeleportGUI = ChestGui.load(this, xmlStream);
        mineTeleportGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    public void teleportToMineZone(Player player, Integer floor) {
        player.teleport(mineZone.get(floor));
        player.sendMessage(Component.text("[시스템] - " + MINE_NAME.get(floor) + "§f(으)로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
        }, 1L);
    }

    public void open(Player player) {
        mineTeleportGUI.show(player);
    }

    public void toMineTeleportZone1(InventoryClickEvent event) {
        teleportToMineZone((Player) event.getWhoClicked(), 0);
    }
    public void toMineTeleportZone2(InventoryClickEvent event) {
        teleportToMineZone((Player) event.getWhoClicked(), 1);
    }
    public void toMineTeleportZone3(InventoryClickEvent event) {
        teleportToMineZone((Player) event.getWhoClicked(), 2);
    }
    public void toMineTeleportZone4(InventoryClickEvent event) {
        teleportToMineZone((Player) event.getWhoClicked(), 3);
    }
}
