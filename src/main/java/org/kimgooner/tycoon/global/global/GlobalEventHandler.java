package org.kimgooner.tycoon.global.global;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.gui.menu.MenuItemUtil;
import org.kimgooner.tycoon.job.mining.MiningStat;
import org.kimgooner.tycoon.job.mining.MiningStatManager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GlobalEventHandler implements Listener {
    private final JavaPlugin plugin;

    private final GlobalDAOController globalDAOController;

    private final Set<UUID> initializedPlayers;
    private final Map<UUID, MiningStat> miningStatMap;
    private final MiningStatManager miningStatManager;

    public GlobalEventHandler(JavaPlugin plugin, GlobalController globalController) {
        this.plugin = plugin;

        this.globalDAOController = globalController.getGlobalDaoController();

        this.initializedPlayers = globalController.getInitializedPlayers();
        this.miningStatMap = globalController.getMiningOverallMap();
        this.miningStatManager = globalController.getMiningController().getMiningStatManager();
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

    public void initStatManager(Player player){
        if(!miningStatMap.containsKey(player.getUniqueId())) {
            miningStatMap.put(player.getUniqueId(), miningStatManager.getCachedStat(player));
            plugin.getLogger().info(player.getName() + "의 초기 채광 스텟 세팅 생성.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            MenuItemUtil.enforceMenuItemSlot(event.getPlayer());
        }, 1L);

        // 멤버
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            initTable(player);
            initStatManager(player);

            Bukkit.getScheduler().runTask(plugin, () -> {
                initializedPlayers.add(player.getUniqueId());
                plugin.getLogger().info(player.getName() + " 초기 세팅 완료.");
            });
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        initializedPlayers.remove(player.getUniqueId());
        plugin.getLogger().info(player.getName() + " 초기 세팅 여부 삭제.");
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
