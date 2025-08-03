package org.kimgooner.tycoon.global.datachest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.global.datachest.combat.CombatDataGUI;
import org.kimgooner.tycoon.global.datachest.farming.FarmingDataGUI;
import org.kimgooner.tycoon.global.datachest.fishing.FishingDataGUI;
import org.kimgooner.tycoon.global.datachest.mining.MiningDataGUI;
import org.kimgooner.tycoon.global.menu.MenuGUI;

public class DataChestEventHandler implements Listener {
    private final MenuGUI menuGUI;
    private final MiningDataGUI miningDataGUI;
    private final FarmingDataGUI farmingDataGUI;
    private final FishingDataGUI fishingDataGUI;
    private final CombatDataGUI combatDataGUI;

    public DataChestEventHandler(MenuGUI menuGUI, MiningDataGUI miningDataGUI, FarmingDataGUI farmingDataGUI, FishingDataGUI fishingDataGUI, CombatDataGUI combatDataGUI){
        this.menuGUI = menuGUI;
        this.miningDataGUI = miningDataGUI;
        this.farmingDataGUI = farmingDataGUI;
        this.fishingDataGUI = fishingDataGUI;
        this.combatDataGUI = combatDataGUI;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(!event.getView().title().equals(Component.text("데이터 보관함").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch (event.getSlot()){
            case 10 -> miningDataGUI.open(player);
            case 11 -> farmingDataGUI.open(player);
            case 12 -> fishingDataGUI.open(player);
            case 13 -> combatDataGUI.open(player);
            case 22 -> menuGUI.open(player);
            default -> {return;}
        }
    }
}
