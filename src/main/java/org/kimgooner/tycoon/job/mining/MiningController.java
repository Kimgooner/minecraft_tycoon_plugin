package org.kimgooner.tycoon.job.mining;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.job.mining.command.HeartCommandHandler;
import org.kimgooner.tycoon.job.mining.command.MiningCommandHandler;
import org.kimgooner.tycoon.job.mining.event.MiningEventHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MiningController {
    private final MiningStatManager miningStatManager;
    private final GlobalController globalController;
    private final MiningPassiveBuff miningPassiveBuff;
    private final MiningUtil miningUtil;

    // Mining 관련
    private final Set<UUID> editingMode = new HashSet<>();
    private final Map<UUID, Boolean> chainBreakingMap = new ConcurrentHashMap<>();
    private final Map<UUID, MiningStat> miningStatCache = new HashMap<>();
    private final Map<UUID, Long> miningStatCacheTime = new HashMap<>();// 5초 캐시 유지

    // Mining 버프 관련
    // 연속 : 속도
    private final Map<UUID, Integer> buffMap_1 = new HashMap<>();
    private final Map<UUID, BukkitTask> resetTask_1 =  new HashMap<>();

    // 연속 : 행운
    private final Map<UUID, Integer> buffMap_2 = new HashMap<>();
    private final Map<UUID, BukkitTask> resetTask_2 =  new HashMap<>();
    public MiningController(JavaPlugin plugin, GlobalController globalController) {
        this.globalController = globalController;

        this.miningStatManager = new MiningStatManager(globalController, plugin, this);
        this.miningUtil = new MiningUtil(plugin, this);
        this.miningPassiveBuff = new MiningPassiveBuff(plugin, this);

        plugin.getServer().getPluginManager().registerEvents(new MiningEventHandler(plugin, this), plugin);
        plugin.getServer().getPluginManager().registerEvents(miningPassiveBuff, plugin);
        plugin.getCommand("mining").setExecutor(new MiningCommandHandler(plugin, globalController, this));
        plugin.getCommand("heart").setExecutor(new HeartCommandHandler(plugin, globalController));
    }

    public Map<UUID, MiningStat> getMiningMap() {return globalController.getMiningOverallMap();}
}
