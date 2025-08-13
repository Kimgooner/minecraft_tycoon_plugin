package org.kimgooner.tycoon.global.gui;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.global.global.SoundUtil;
import org.kimgooner.tycoon.global.gui.datachest.gui.*;
import org.kimgooner.tycoon.global.gui.job.mining.MineTeleportGUI;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartEventHandler;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartGUI;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartUpEventHandler;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartUpGUI;
import org.kimgooner.tycoon.global.gui.menu.MenuGUI;
import org.kimgooner.tycoon.job.mining.MiningController;

@Getter
public class GlobalGUIController {
    private final SoundUtil soundUtil = new SoundUtil();
    private final MenuGUI menuGUI;

    private final DataChestGUI dataChestGUI;
    private final MiningDataGUI miningDataGUI;
    private final FarmingDataGUI farmingDataGUI;
    private final FishingDataGUI fishingDataGUI;
    private final CombatDataGUI combatDataGUI;

    private final MineTeleportGUI mineTeleportGUI;
    private final CaveHeartGUI caveHeartGUI;
    private final CaveHeartUpGUI caveHeartUpGUI;

    public GlobalGUIController(
            JavaPlugin plugin,
            GlobalDAOController globalDAOController,
            MiningController miningController,
            GlobalController globalController
    )
    {
        plugin.getServer().getPluginManager().registerEvents(new GlobalGUIHandler(), plugin);
        this.menuGUI = new MenuGUI(plugin,
                this,
                globalDAOController,
                miningController);

        this.miningDataGUI = new MiningDataGUI(plugin, globalController, this);
        this.farmingDataGUI = new FarmingDataGUI(plugin, globalController, this);
        this.combatDataGUI = new CombatDataGUI(plugin, this);
        this.fishingDataGUI = new FishingDataGUI(plugin, globalController, this);
        this.dataChestGUI = new DataChestGUI(plugin, this);

        this.mineTeleportGUI = new MineTeleportGUI(plugin, globalController);

        this.caveHeartGUI = new CaveHeartGUI(plugin, this, globalController);
        this.caveHeartUpGUI = new CaveHeartUpGUI(plugin, this);
        plugin.getServer().getPluginManager().registerEvents(new CaveHeartEventHandler(plugin, globalController), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CaveHeartUpEventHandler(), plugin);
    }

    public void closeInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.closeInventory();
    }

    public void openMineTeleportGUI(Player player) {
        soundUtil.playGUISound(player);
        mineTeleportGUI.open(player);
    }

    public void openMenu(Player player) {
        soundUtil.playGUISound(player);
        menuGUI.open(player);
    }

    public void openDataChest(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        soundUtil.playGUISound(p);
        dataChestGUI.open(p);
    }

    public void openMiningData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        soundUtil.playGUISound(p);
        miningDataGUI.open(p);
    }

    public void openFarmingData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        soundUtil.playGUISound(p);
        farmingDataGUI.open(p);
    }

    public void openFishingData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        soundUtil.playGUISound(p);
        fishingDataGUI.open(p);
    }

    public void openCombatData(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        soundUtil.playGUISound(p);
        combatDataGUI.open(p);
    }

    public void openCaveHeart(Player player) {
        soundUtil.playGUISound(player);
        caveHeartGUI.open(player);
    }

    public void openCaveHeartUp(Player player) {
        soundUtil.playGUISound(player);
        caveHeartUpGUI.open(player);
    }
}
