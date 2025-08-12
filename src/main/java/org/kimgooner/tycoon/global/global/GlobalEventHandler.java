package org.kimgooner.tycoon.global.global;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.gui.menu.MenuItemUtil;

public class GlobalEventHandler implements Listener {
    private final GlobalDAOController globalDAOController;
    private final JavaPlugin plugin;

    public GlobalEventHandler(GlobalDAOController globalDAOController, JavaPlugin plugin) {
        this.globalDAOController = globalDAOController;
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.BAT) {
            event.setCancelled(true);
        }
        if (event.getEntityType() == EntityType.WANDERING_TRADER) {
            event.setCancelled(true);
        }
        if (event.getEntityType() == EntityType.LLAMA) {
            event.setCancelled(true);
        }
    }

    public void initTable(Player player) {
        plugin.getLogger().info(player.getName() + "접속, DB 확인.");
        plugin.getLogger().info("-----------------------------");
        if(!globalDAOController.getMemberDAO().hasData(player)) {
            globalDAOController.getMemberDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 멤버 DB 생성");
        }

        // 채광
        if(!globalDAOController.getMiningDAO().hasData(player)) {
            globalDAOController.getMiningDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 채광 DB 생성");
        }
        if(!globalDAOController.getHeartDAO().hasData(player)) {
            globalDAOController.getHeartDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 동굴의 심장 DB 생성");
        }
        if(!globalDAOController.getHeartInfoDAO().hasData(player)) {
            globalDAOController.getHeartInfoDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 동굴의 심장 정보 DB 생성");
        }

        // 농사
        if(!globalDAOController.getFarmingDAO().hasData(player)) {
            globalDAOController.getFarmingDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 농사 DB 생성");
        }

        // 낚시
        if(!globalDAOController.getFishingDAO().hasData(player)) {
            globalDAOController.getFishingDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 낚시 DB 생성");
        }

        // 전투
        if(!globalDAOController.getCombatDAO().hasData(player)) {
            globalDAOController.getCombatDAO().init(player);
            plugin.getLogger().info(player.getName() + "의 전투 DB 생성");
        }

        // 데이터 보관함
        for (int categoryId = 1; categoryId <= 4; categoryId++) {
            if(!globalDAOController.getDataStorageDAO().hasData(player, categoryId)) {
                globalDAOController.getDataStorageDAO().init(player, categoryId);
                plugin.getLogger().info(player.getName() + "의 데이터 보관함 DB 생성, type: " + categoryId);
            }
        }
        plugin.getLogger().info("-----------------------------");
        plugin.getLogger().info(player.getName() + "의 DB 확인 완료.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            MenuItemUtil.enforceMenuItemSlot(event.getPlayer());
        }, 1L);

        // 멤버
        Player player = event.getPlayer();
        initTable(player);
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
