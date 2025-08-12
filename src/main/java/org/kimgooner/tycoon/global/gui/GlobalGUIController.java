package org.kimgooner.tycoon.global.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.*;
import org.kimgooner.tycoon.db.dao.job.combat.CombatDAO;
import org.kimgooner.tycoon.db.dao.job.farming.FarmingDAO;
import org.kimgooner.tycoon.db.dao.job.fishing.FishingDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.global.global.SoundUtil;
import org.kimgooner.tycoon.global.gui.datachest.gui.*;
import org.kimgooner.tycoon.global.gui.job.mining.*;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartEventHandler;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartGUI;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartUpEventHandler;
import org.kimgooner.tycoon.global.gui.job.mining.heart.CaveHeartUpGUI;
import org.kimgooner.tycoon.global.gui.menu.MenuGUI;

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
            MemberDAO memberDAO,
            MiningDAO miningDAO,
            FarmingDAO farmingDAO,
            FishingDAO fishingDAO,
            CombatDAO combatDAO,
            DataStorageDAO dataStorageDAO,
            HeartDAO heartDAO,
            HeartInfoDAO heartInfoDAO
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

        this.caveHeartGUI = new CaveHeartGUI(plugin, heartDAO,heartInfoDAO, this);
        this.caveHeartUpGUI = new CaveHeartUpGUI(plugin, this);
        plugin.getServer().getPluginManager().registerEvents(new CaveHeartEventHandler(plugin, heartDAO, heartInfoDAO), plugin);
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
