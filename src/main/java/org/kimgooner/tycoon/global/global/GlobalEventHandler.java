package org.kimgooner.tycoon.global.global;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.menu.MenuItemUtil;

public class GlobalEventHandler implements Listener {
    private final JavaPlugin plugin;

    public GlobalEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            MenuItemUtil.enforceMenuItemSlot(event.getPlayer());
        }, 1L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            MenuItemUtil.enforceMenuItemSlot(event.getPlayer());
        }, 1L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        event.setKeepInventory(true);
        event.getDrops().clear();
    }
}
