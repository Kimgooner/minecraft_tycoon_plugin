package org.kimgooner.tycoon.job.mining.controller;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.job.mining.command.HeartCommandHandler;
import org.kimgooner.tycoon.job.mining.command.MiningCommandHandler;
import org.kimgooner.tycoon.job.mining.dto.MiningDataRequestDto;
import org.kimgooner.tycoon.job.mining.event.MiningEventHandler;
import org.kimgooner.tycoon.job.mining.event.MiningPortalEventHandler;
import org.kimgooner.tycoon.job.mining.model.MiningStat;
import org.kimgooner.tycoon.job.mining.service.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MiningController {
    private final GlobalController globalController;

    // Mining 관련
    private final Set<UUID> editingMode = new HashSet<>();
    private final Set<UUID> statMode = new HashSet<>();
    private final Map<UUID, MiningStat> miningStatCache = new HashMap<>();
    private final Map<UUID, Long> miningStatCacheTime = new HashMap<>();// 5초 캐시 유지
    private final Map<UUID, Boolean> chainBreakingMap = new ConcurrentHashMap<>();

    // Mining 버프 관련
    // 연속 : 속도
    private final Map<UUID, Integer> buffMap_1 = new HashMap<>();
    // 연속 : 행운
    private final Map<UUID, Integer> buffMap_2 = new HashMap<>();

    private final MiningBuffService miningBuffService;
    private final MiningStatService miningStatService;
    private final MiningDropService miningDropService;
    private final MiningDataService miningDataService;
    private final MiningAttributeService miningAttributeService;
    private final BlockRegenService blockRegenService;
    private final BlockSpreadService blockSpreadService;
    private final MiningPortalEventHandler miningPortalEventHandler;

    public MiningController(JavaPlugin plugin, GlobalController globalController) {
        this.globalController = globalController;
        this.miningStatService = new MiningStatService(plugin,
                globalController.getGlobalDaoController().getMiningDAO(),
                globalController.getGlobalDaoController().getHeartDAO(),
                globalController.getGlobalDaoController().getHeartInfoDAO(),
                buffMap_1,
                buffMap_2
                );

        this.miningBuffService = new MiningBuffService(plugin, buffMap_1, buffMap_2);
        this.miningDropService = new MiningDropService();
        this.miningDataService = new MiningDataService(
                plugin,
                globalController.getGlobalDaoController().getMiningDAO(),
                globalController.getGlobalDaoController().getHeartDAO(),
                globalController.getGlobalDaoController().getHeartInfoDAO(),
                globalController.getGlobalDaoController().getDataStorageDAO()
        );
        this.miningAttributeService = new MiningAttributeService();
        this.blockRegenService = new BlockRegenService(plugin);
        this.blockSpreadService = new BlockSpreadService(plugin, miningDropService, blockRegenService);

        plugin.getServer().getPluginManager().registerEvents(new MiningEventHandler(plugin,
                this,
                editingMode,
                miningStatService,
                miningBuffService,
                miningDropService,
                miningDataService,
                miningAttributeService,
                blockRegenService,
                blockSpreadService), plugin);
        this.miningPortalEventHandler = new MiningPortalEventHandler(plugin);
        plugin.getServer().getPluginManager().registerEvents(miningPortalEventHandler, plugin);
        plugin.getCommand("mining").setExecutor(new MiningCommandHandler(plugin, globalController, this, miningPortalEventHandler));
        plugin.getCommand("heart").setExecutor(new HeartCommandHandler(plugin, globalController));
    }

    public Map<UUID, MiningStat> getMiningMap() {return globalController.getMiningOverallMap();}
    public MiningStat getMiningStat(Player player) {
        UUID uuid = player.getUniqueId();
        Map<UUID, MiningStat> miningMap = getMiningMap();
        MiningStat miningStat = miningMap.get(uuid);

        if(miningStat == null) {
            MiningDataRequestDto dto = miningDataService.getPlayerData(player);
            miningStat = miningStatService.getCachedStat(player, 0, dto);
            miningMap.put(uuid, miningStat);
        }
        return miningStat;
    }
}
