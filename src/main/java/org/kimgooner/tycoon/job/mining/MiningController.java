package org.kimgooner.tycoon.job.mining;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.job.mining.handler.MiningCommandHandler;
import org.kimgooner.tycoon.job.mining.handler.MiningEventHandler;
import org.kimgooner.tycoon.job.mining.heart.HeartCommandHandler;

public class MiningController {
    private final JavaPlugin plugin;
    private final GlobalDAOController globalDaoController;
    private final GlobalGUIController globalGUIController;

    public MiningController(JavaPlugin plugin, GlobalDAOController globalDaoController, GlobalGUIController globalGUIController) {
        this.plugin = plugin;
        this.globalDaoController =  globalDaoController;
        this.globalGUIController = globalGUIController;

        plugin.getServer().getPluginManager().registerEvents(new MiningEventHandler(globalDaoController.getMiningDAO(), globalDaoController.getHeartDAO(), globalDaoController.getHeartInfoDAO(), globalDaoController.getDataStorageDAO(), plugin), plugin);
        plugin.getCommand("mining").setExecutor(new MiningCommandHandler(plugin, globalDaoController.getMiningDAO()));
        plugin.getCommand("heart").setExecutor(new HeartCommandHandler(plugin, globalDaoController.getHeartDAO(), globalDaoController.getHeartInfoDAO()));
    }
}
