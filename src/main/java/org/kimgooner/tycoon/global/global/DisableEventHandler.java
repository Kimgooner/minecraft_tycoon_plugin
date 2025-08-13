package org.kimgooner.tycoon.global.global;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.kimgooner.tycoon.GlobalController;

import java.util.Set;
import java.util.UUID;

public class DisableEventHandler implements Listener {
    private final Set<UUID> initializedPlayers;

    public DisableEventHandler(GlobalController globalController) {
        initializedPlayers = globalController.getInitializedPlayers();
    }

    private boolean isInitialized(UUID uuid) {
        return initializedPlayers.contains(uuid);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isInitialized(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c초기 세팅이 완료될 때까지 블록을 부술 수 없습니다.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isInitialized(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c초기 세팅이 완료될 때까지 블록을 놓을 수 없습니다.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isInitialized(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c초기 세팅이 완료될 때까지 상호작용할 수 없습니다.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
            UUID uuid = event.getWhoClicked().getUniqueId();
            if (!isInitialized(uuid)) {
                event.setCancelled(true);
                ((org.bukkit.entity.Player) event.getWhoClicked()).sendMessage("§c초기 세팅이 완료될 때까지 인벤토리를 조작할 수 없습니다.");
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof org.bukkit.entity.Player) {
            UUID uuid = event.getPlayer().getUniqueId();
            if (!isInitialized(uuid)) {
                event.setCancelled(true);
                ((org.bukkit.entity.Player) event.getPlayer()).sendMessage("§c초기 세팅이 완료될 때까지 인벤토리를 열 수 없습니다.");
            }
        }
    }
}
