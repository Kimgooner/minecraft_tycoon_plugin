package org.kimgooner.tycoon.global.gui.datachest.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.global.SoundUtil;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

import java.io.InputStream;

public class DataChestGUI {
    private final ChestGui dataChestGUI;
    private final GlobalGUIController globalGuiController;
    private final SoundUtil soundUtil = new SoundUtil();

    public DataChestGUI(JavaPlugin plugin, GlobalGUIController globalGuiController) {
        this.globalGuiController = globalGuiController;

        InputStream xmlStream = plugin.getResource("gui/datachest/datachest.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }

        dataChestGUI = ChestGui.load(this, xmlStream);
        dataChestGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    public void open(Player player) {
        dataChestGUI.show(player);
    }

    public void toMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.openMenu(player);
    }

    public void toMining(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.openMiningData(event);
    }

    public void toFarming(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.openFarmingData(event);
    }

    public void toFishing(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.openFishingData(event);
    }

    public void toCombat(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGuiController.openCombatData(event);
    }
}
