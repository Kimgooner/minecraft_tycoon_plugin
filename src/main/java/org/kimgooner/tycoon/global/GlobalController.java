package org.kimgooner.tycoon.global;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.global.GlobalEventHandler;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.gui.menu.MenuEventHandler;
import org.kimgooner.tycoon.global.npc.GlobalNPCController;
import org.kimgooner.tycoon.global.warp.GlobalWarpController;
import org.kimgooner.tycoon.job.mining.MiningCommandHandler;
import org.kimgooner.tycoon.job.mining.MiningEventHandler;

import java.sql.Connection;

public class GlobalController {
    private final GlobalDAOController globalDaoController;
    private final GlobalGUIController globalGuiController;
    private final GlobalWarpController globalWarpController;
    private final GlobalNPCController globalNPCController;

    public GlobalController(JavaPlugin plugin, Connection connection) {
        this.globalDaoController = new GlobalDAOController(connection);
        this.globalGuiController = new GlobalGUIController(
                plugin,
                globalDaoController.getMemberDAO(),
                globalDaoController.getMiningDAO(),
                globalDaoController.getFarmingDAO(),
                globalDaoController.getFishingDAO(),
                globalDaoController.getCombatDAO(),
                globalDaoController.getDataStorageDAO()
        );
        this.globalWarpController = new GlobalWarpController(plugin, globalGuiController);
        this.globalNPCController = new GlobalNPCController(plugin, globalGuiController);

        //이벤트 핸들러
        plugin.getServer().getPluginManager().registerEvents(new GlobalEventHandler(globalDaoController, plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MiningEventHandler(globalDaoController.getMiningDAO(), globalDaoController.getDataStorageDAO(), plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MenuEventHandler(globalGuiController), plugin);

        //커맨드 핸들러
        plugin.getCommand("mining").setExecutor(new MiningCommandHandler(plugin, globalDaoController.getMiningDAO()));

    }
    public GlobalDAOController getDaoController() {
        return globalDaoController;
    }
    public GlobalGUIController getGuiController() {
        return globalGuiController;
    }
    public GlobalWarpController getGlobalWarpController() {
        return globalWarpController;
    }
}
