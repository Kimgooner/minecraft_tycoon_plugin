package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningBuffService {
    private final JavaPlugin plugin;

    // 연속 : 속도
//    private final int MAX_SPEED = 200;
//    private final Map<UUID, Integer> buffMap_1;
//    private final Map<UUID, BukkitTask> resetTask_1;
//
//    // 연속 : 행운
//    private final int MAX_FORTUNE = 50;
//    private final Map<UUID, Integer> buffMap_2;
//    private final Map<UUID, BukkitTask> resetTask_2;

    private final int MAX_SPEED = 200;
    private final int MAX_FORTUNE = 50;
    private final Map<UUID, Integer> buffMap_1;
    private final Map<UUID, Integer> buffMap_2;
    private final Map<UUID, BukkitTask> resetTask_1= new HashMap<>();
    private final Map<UUID, BukkitTask> resetTask_2 = new HashMap<>();

    public MiningBuffService(JavaPlugin plugin,
                             Map<UUID, Integer> buffMap_1, Map<UUID, Integer> buffMap_2
    ) {
        this.plugin = plugin;


        this.buffMap_1 = buffMap_1;
        this.buffMap_2 = buffMap_2;
    }

    public void consecutiveSpeed(Player player, boolean isConsecutive) {
        UUID uuid = player.getUniqueId();

        if(!isConsecutive) {return;}

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

    public void consecutiveFortune(Player player, boolean isConsecutive) {
        UUID uuid = player.getUniqueId();

        if (!isConsecutive) {
            return;
        }

        int currentFortune = buffMap_2.getOrDefault(uuid, 0);
        if(currentFortune == 0) {player.sendMessage("§f[시스템] - §6연속적인 채광: 행운 §f패시브가 발동되었습니다rmse!");}
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
        resetTask_2.put(uuid, resetTask);
    }
}

