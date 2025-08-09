package org.kimgooner.tycoon.global;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DAOController;
import org.kimgooner.tycoon.global.global.GlobalEventHandler;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.gui.menu.MenuEventHandler;
import org.kimgooner.tycoon.global.warp.GlobalWarpController;
import org.kimgooner.tycoon.job.mining.MiningCommandHandler;
import org.kimgooner.tycoon.job.mining.MiningEventHandler;

import java.sql.Connection;

public class GlobalController {
    private final DAOController daoController;
    private final GlobalGUIController globalGuiController;
    private final GlobalWarpController globalWarpController;

    public GlobalController(JavaPlugin plugin, Connection connection) {
        this.daoController = new DAOController(connection);
        this.globalGuiController = new GlobalGUIController(
                plugin,
                daoController.getMemberDAO(),
                daoController.getMiningDAO(),
                daoController.getFarmingDAO(),
                daoController.getFishingDAO(),
                daoController.getCombatDAO(),
                daoController.getDataStorageDAO()
        );
        this.globalWarpController = new GlobalWarpController(plugin, globalGuiController);

        //이벤트 핸들러
        plugin.getServer().getPluginManager().registerEvents(new GlobalEventHandler(daoController.getDataStorageDAO(), plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MiningEventHandler(daoController.getMiningDAO(), daoController.getDataStorageDAO(), plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MenuEventHandler(globalGuiController), plugin);

        //커맨드 핸들러
        plugin.getCommand("mining").setExecutor(new MiningCommandHandler(plugin, daoController.getMiningDAO()));

    }
    public DAOController getDaoController() {
        return daoController;
    }
    public GlobalGUIController getGuiController() {
        return globalGuiController;
    }
    public GlobalWarpController getGlobalWarpController() {
        return globalWarpController;
    }
}
