package org.kimgooner.tycoon.global.npc;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.npc.mining.MiningNPCHandler;

public class GlobalNPCController {
    public GlobalNPCController(JavaPlugin plugin, GlobalGUIController globalGUIController) {
        plugin.getServer().getPluginManager().registerEvents(new MiningNPCHandler(plugin, globalGUIController), plugin);
    }
}
