package org.kimgooner.tycoon.job.mining;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class MiningPassiveBuff implements Listener {
    private final int MAX_SPEED = 200;
    private final int MAX_FORTUNE = 50;

    // 연속 : 속도
    private final Map<UUID, Integer> buffMap_1;
    private final Map<UUID, BukkitTask> resetTask_1;

    // 연속 : 행운
    private final Map<UUID, Integer> buffMap_2;
    private final Map<UUID, BukkitTask> resetTask_2;

    private final JavaPlugin plugin;
    private final MiningStatManager miningStatManager;

    public MiningPassiveBuff(JavaPlugin plugin, MiningController miningController) {
        this.plugin = plugin;
        this.miningStatManager = miningController.getMiningStatManager();

        this.buffMap_1 = miningController.getBuffMap_1();
        this.buffMap_2 = miningController.getBuffMap_2();
        this.resetTask_1 = miningController.getResetTask_1();
        this.resetTask_2 = miningController.getResetTask_2();
    }

    public void consecutiveSpeed(Player player) {
        UUID uuid = player.getUniqueId();

        MiningStat miningStat = miningStatManager.getCachedStat(player);
        if(!miningStat.consecutiveSpeed()) {return;}

        int currentSpeed = buffMap_1.getOrDefault(uuid, 0);
        if(currentSpeed == 0) {player.sendMessage("§f[시스템] - §6연속적인 채광: 속도 §f패시브가 발동되었습니다!");}
        if(currentSpeed < MAX_SPEED) {
            buffMap_1.put(uuid, currentSpeed + 1);
        }

        if (resetTask_1.containsKey(uuid)) {
            resetTask_1.get(uuid).cancel();
        }

        BukkitTask resetTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            buffMap_1.remove(uuid);
            resetTask_1.remove(uuid);
            player.sendMessage("§f[시스템] - §6연속적인 채광: 속도 §f패시브가 만료되었습니다...");
        }, 60L);

        resetTask_1.put(uuid, resetTask);
    }

    public void consecutiveFortune(Player player) {
        UUID uuid = player.getUniqueId();

        MiningStat miningStat = miningStatManager.getCachedStat(player);
        if (!miningStat.consecutiveFortune()) {
            return;
        }

        int currentFortune = buffMap_2.getOrDefault(uuid, 0);
        if(currentFortune == 0) {player.sendMessage("§f[시스템] - §6연속적인 채광: 행운 §f패시브가 발동되었습니다!");}
        if(currentFortune < MAX_FORTUNE) {
            buffMap_2.put(uuid, currentFortune + 1);
        }

        if (resetTask_2.containsKey(uuid)) {
            resetTask_2.get(uuid).cancel();
        }

        BukkitTask resetTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            buffMap_2.remove(uuid);
            resetTask_2.remove(uuid);
            player.sendMessage("§f[시스템] - §6연속적인 채광: 행운 §f패시브가 만료되었습니다...");
        }, 60L);
        resetTask_1.put(uuid, resetTask);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        buffMap_1.remove(uuid);
        buffMap_2.remove(uuid);
        resetTask_1.remove(uuid);
        resetTask_2.remove(uuid);
    }
}
