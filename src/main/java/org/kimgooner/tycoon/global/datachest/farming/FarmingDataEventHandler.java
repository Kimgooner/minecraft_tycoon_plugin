package org.kimgooner.tycoon.global.datachest.farming;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.datachest.DataChestGUI;

public class FarmingDataEventHandler implements Listener {
    private final DataChestGUI dataChestGUI;

    public FarmingDataEventHandler(DataChestGUI dataChestGUI){
        this.dataChestGUI = dataChestGUI;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(!event.getView().title().equals(Component.text("데이터 보관함 - 농사").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch (event.getSlot()){
            case 40 -> dataChestGUI.open(player);
            default -> {return;}
        }
    }
}
