package org.kimgooner.tycoon.global.warp.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

public class MiningWarpHandler implements Listener {
    private final JavaPlugin plugin;
    private final GlobalGUIController globalGuiController;

    public MiningWarpHandler(JavaPlugin plugin, GlobalGUIController globalGuiController) {
        this.plugin = plugin;
        this.globalGuiController = globalGuiController;
    }

    World world = Bukkit.getWorld("world");
    Location miningHub = new Location(world, 0.5, 65, 417.5, 0, 0);

    public void teleportToMineHub(Player player) {
        player.teleport(miningHub);
        player.sendMessage(Component.text("[시스템] - §f§l§n광산 허브§f(으)로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
        }, 1L);
    }

    public void openMineTeleport(Player player) {
        globalGuiController.openMineTeleportGUI(player);
    }
}
