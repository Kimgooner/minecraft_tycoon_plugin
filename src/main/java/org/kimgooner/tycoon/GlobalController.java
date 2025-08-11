package org.kimgooner.tycoon;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.global.GlobalEventHandler;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.gui.menu.MenuEventHandler;
import org.kimgooner.tycoon.global.npc.GlobalNPCController;
import org.kimgooner.tycoon.global.warp.GlobalWarpController;
import org.kimgooner.tycoon.job.mining.MiningController;

import java.sql.Connection;

public class GlobalController {
    private final GlobalDAOController globalDaoController;
    private final GlobalGUIController globalGuiController;
    private final GlobalWarpController globalWarpController;
    private final GlobalNPCController globalNPCController;

    private final MiningController miningController;

    public GlobalController(JavaPlugin plugin, Connection connection) {
        this.globalDaoController = new GlobalDAOController(connection, plugin);
        this.globalGuiController = new GlobalGUIController(
                plugin,
                globalDaoController.getMemberDAO(),
                globalDaoController.getMiningDAO(),
                globalDaoController.getFarmingDAO(),
                globalDaoController.getFishingDAO(),
                globalDaoController.getCombatDAO(),
                globalDaoController.getDataStorageDAO(),
                globalDaoController.getHeartDAO(),
                globalDaoController.getHeartInfoDAO()
        );
        this.globalWarpController = new GlobalWarpController(plugin, globalGuiController);
        this.globalNPCController = new GlobalNPCController(plugin, globalGuiController);
        this.miningController = new MiningController(plugin, globalDaoController, globalGuiController);

        //이벤트 핸들러
        plugin.getServer().getPluginManager().registerEvents(new GlobalEventHandler(globalDaoController, plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MenuEventHandler(globalGuiController), plugin);

        //커맨드 핸들러


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
