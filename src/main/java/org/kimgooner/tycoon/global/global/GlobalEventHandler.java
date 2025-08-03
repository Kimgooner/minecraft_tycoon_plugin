package org.kimgooner.tycoon.global.global;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.global.menu.MenuItemUtil;

public class GlobalEventHandler implements Listener {
    private final DataStorageDAO dataStorageDAO;
    private final JavaPlugin plugin;

    public GlobalEventHandler(DataStorageDAO dataStorageDAO, JavaPlugin plugin) {
        this.dataStorageDAO = dataStorageDAO;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            MenuItemUtil.enforceMenuItemSlot(event.getPlayer());
        }, 1L);

        Player player = event.getPlayer();
        for (int categoryId = 1; categoryId <= 4; categoryId++) {
            if (!dataStorageDAO.hasData(player, categoryId)) {
                dataStorageDAO.initializeDataStorage(player, categoryId);
            }
        }
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
