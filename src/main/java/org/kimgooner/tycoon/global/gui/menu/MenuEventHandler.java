package org.kimgooner.tycoon.global.gui.menu;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

public class MenuEventHandler implements Listener {
    private final GlobalGUIController controller;
    public MenuEventHandler(GlobalGUIController controller) {
        this.controller = controller;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (MenuItemUtil.isMenuItem(event.getCurrentItem()) || MenuItemUtil.isMenuItem(event.getCursor())) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (int slot : event.getRawSlots()) {
            if (MenuItemUtil.isMenuItem(event.getOldCursor())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (MenuItemUtil.isMenuItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (MenuItemUtil.isMenuItem(event.getMainHandItem()) || MenuItemUtil.isMenuItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(MenuItemUtil.isMenuItem(event.getItem())){
            event.setCancelled(true);
            controller.openMenu(event.getPlayer());
        }
    }
}