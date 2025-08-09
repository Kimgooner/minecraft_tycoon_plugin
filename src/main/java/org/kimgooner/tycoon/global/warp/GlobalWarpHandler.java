package org.kimgooner.tycoon.global.warp;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalWarpHandler implements Listener {
    private final GlobalWarpController globalWarpController;

    GlobalWarpHandler(GlobalWarpController globalWarpController) {
        this.globalWarpController = globalWarpController;
    }

    private final Map<UUID, Set<String>> playerRegions = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 블럭 단위 위치 변화가 없으면 무시
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
        if (regionManager == null) return;

        Location location = player.getLocation();
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        Set<String> currentRegionIds = regionSet.getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toSet());

        UUID uuid = player.getUniqueId();
        Set<String> previousRegionIds = playerRegions.getOrDefault(uuid, new HashSet<>());

        // 새롭게 들어간 리전들만 탐지
        Set<String> newlyEntered = new HashSet<>(currentRegionIds);
        newlyEntered.removeAll(previousRegionIds);

        for (String regionId : newlyEntered) {
            if (globalWarpController.getRegionActions().containsKey(regionId)) {
                globalWarpController.getRegionActions().get(regionId).accept(player);
            }
        }
        // 현재 리전 상태 갱신
        playerRegions.put(uuid, currentRegionIds);
    }
}
