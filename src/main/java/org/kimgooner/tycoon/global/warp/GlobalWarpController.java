package org.kimgooner.tycoon.global.warp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.warp.mining.MiningWarpHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GlobalWarpController {
    private final JavaPlugin plugin;

    private final Map<String, Consumer<Player>> regionActions = new HashMap<>();

    private final MiningWarpHandler miningWarpHandler;

    public GlobalWarpController(JavaPlugin plugin, GlobalGUIController globalGuiController) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new GlobalWarpHandler(this), plugin);


        this.miningWarpHandler = new MiningWarpHandler(plugin, globalGuiController);

        regionActions.put("mine_enter_zone", miningWarpHandler::openMineTeleport);
        regionActions.put("mine1_leave_zone", miningWarpHandler::teleportToMineHub);
        regionActions.put("mine2_leave_zone", miningWarpHandler::teleportToMineHub);
        regionActions.put("mine3_leave_zone", miningWarpHandler::teleportToMineHub);
        regionActions.put("mine4_leave_zone", miningWarpHandler::teleportToMineHub);
    }

    public Map<String, Consumer<Player>> getRegionActions() {
        return regionActions;
    }
}
