package org.kimgooner.tycoon;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.global.DisableEventHandler;
import org.kimgooner.tycoon.global.global.GlobalEventHandler;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.gui.menu.MenuEventHandler;
import org.kimgooner.tycoon.global.npc.GlobalNPCController;
import org.kimgooner.tycoon.global.warp.GlobalWarpController;
import org.kimgooner.tycoon.job.mining.controller.MiningController;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.*;

@Getter
public class GlobalController {
    private final GlobalDAOController globalDaoController;
    private final GlobalGUIController globalGuiController;
    private final GlobalWarpController globalWarpController;
    private final GlobalNPCController globalNPCController;

    private final MiningController miningController;

    private final Set<UUID> initializedPlayers = new HashSet<>();
    private final Map<UUID, MiningStat> miningOverallMap = new HashMap<>();
//    private final Map<UUID, MiningOverall> farmingOverallMap = new HashMap<>();
//    private final Map<UUID, MiningOverall> fishingOverallMap = new HashMap<>();
//    private final Map<UUID, MiningOverall> combatOverallMap = new HashMap<>();
    public GlobalController(JavaPlugin plugin, DatabaseManager databaseManager) {
        this.globalDaoController = new GlobalDAOController(databaseManager, plugin);
        this.miningController = new MiningController(plugin,this);
        this.globalGuiController = new GlobalGUIController(plugin, globalDaoController, miningController, this);
        this.globalWarpController = new GlobalWarpController(plugin, globalGuiController);
        this.globalNPCController = new GlobalNPCController(plugin, globalGuiController);


        //이벤트 핸들러
        plugin.getServer().getPluginManager().registerEvents(new GlobalEventHandler(plugin, this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new DisableEventHandler(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MenuEventHandler(globalGuiController), plugin);

        //커맨드 핸들러
    }
}
