package org.kimgooner.tycoon.global.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.*;
import org.kimgooner.tycoon.global.gui.datachest.gui.*;
import org.kimgooner.tycoon.global.gui.job.mining.MineTeleportGUI;
import org.kimgooner.tycoon.global.gui.menu.MenuGUI;

public class GlobalGUIController {
    private final MenuGUI menuGUI;
    private final DataChestGUI dataChestGUI;
    private final MiningDataGUI miningDataGUI;
    private final FarmingDataGUI farmingDataGUI;
    private final FishingDataGUI fishingDataGUI;
    private final CombatDataGUI combatDataGUI;

    private final MineTeleportGUI mineTeleportGUI;

    public GlobalGUIController(
            JavaPlugin plugin,
            MemberDAO memberDAO,
            MiningDAO miningDAO,
            FarmingDAO farmingDAO,
            FishingDAO fishingDAO,
            CombatDAO combatDAO,
            DataStorageDAO dataStorageDAO
    )
    {
        plugin.getServer().getPluginManager().registerEvents(new GlobalGUIHandler(), plugin);

        this.menuGUI = new MenuGUI(plugin, this, memberDAO, miningDAO, farmingDAO, fishingDAO, combatDAO);
        this.dataChestGUI = new DataChestGUI(plugin, this);
        this.miningDataGUI = new MiningDataGUI(plugin, dataStorageDAO, this);
        this.farmingDataGUI = new FarmingDataGUI(plugin, dataStorageDAO, this);
        this.combatDataGUI = new CombatDataGUI(plugin, dataStorageDAO, this);
        this.fishingDataGUI = new FishingDataGUI(plugin, dataStorageDAO, this);

        this.mineTeleportGUI = new MineTeleportGUI(plugin, this);
    }

    public void closeInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.closeInventory();
    }

    public void openMineTeleportGUI(Player player) {
        mineTeleportGUI.open(player);
    }

    public void openMenu(Player player) {
        menuGUI.open(player);
    }

    public void openDataChest(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        dataChestGUI.open(p);
    }

    public void openMiningData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        miningDataGUI.open(p);
    }

    public void openFarmingData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        farmingDataGUI.open(p);
    }

    public void openFishingData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        fishingDataGUI.open(p);
    }

    public void openCombatData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        combatDataGUI.open(p);
    }
}
