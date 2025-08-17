package org.kimgooner.tycoon.job.mining.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.job.mining.model.PortalBuffZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningPortalEventHandler implements Listener {
    private final JavaPlugin plugin;

    public MiningPortalEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final Map<Player, PortalBuffZone> activePortals = new HashMap<>();

    public void spawnPortalInFront(Player player) {
        Location eye = player.getEyeLocation();
        Location portalLoc = eye.add(eye.getDirection().normalize().multiply(1));
        PortalBuffZone currentPortal = new PortalBuffZone(portalLoc, 0.5, 0.5, 15);
        currentPortal.start(plugin);
        activePortals.put(player, currentPortal);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        PortalBuffZone portal = activePortals.get(player);
        if (portal == null) return;

        List<Location> targets = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            targets.add(player.getEyeLocation().add(player.getLocation().getDirection().multiply(i)));
        }

        // 포탈 범위 체크
        for(Location target : targets) {
            if (target.distanceSquared(portal.getCenter()) <= portal.getRadius() * portal.getRadius()) {
                enterSpecialMine(player);
                activePortals.remove(player);
                portal.removePortal();
            }
        }
    }

    private void enterSpecialMine(Player player) {
        Location mineLoc = new Location(player.getWorld(), 0, 65, 417);
        player.teleport(mineLoc);
        player.sendMessage("§6포탈을 통해 특별 광산에 입장했습니다!");
    }
}
