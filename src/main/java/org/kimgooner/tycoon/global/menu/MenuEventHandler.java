package org.kimgooner.tycoon.global.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.datachest.DataChestGUI;

public class MenuEventHandler implements Listener {
    private final MenuGUI menuGUI;
    private final DataChestGUI dataChestGUI;

    public MenuEventHandler(MenuGUI menuGUI, DataChestGUI dataChestGUI){
        this.menuGUI = menuGUI;
        this.dataChestGUI = dataChestGUI;
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
            menuGUI.open(event.getPlayer());
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(!event.getView().title().equals(Component.text("메뉴").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch (event.getSlot()){
            case 30 -> dataChestGUI.open(player);
        }
    }
}