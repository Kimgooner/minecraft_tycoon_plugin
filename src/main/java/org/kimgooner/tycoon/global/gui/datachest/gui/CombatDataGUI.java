package org.kimgooner.tycoon.global.gui.datachest.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

import java.io.InputStream;

public class CombatDataGUI {
    private final DataStorageDAO dataStorageDAO;
    private final GlobalGUIController globalGuiController;

    private final ChestGui combatDataGUI;

    public CombatDataGUI(JavaPlugin plugin, DataStorageDAO dataStorageDAO, GlobalGUIController globalGuiController) {
        this.dataStorageDAO = dataStorageDAO;
        this.globalGuiController = globalGuiController;

        InputStream xmlStream = plugin.getResource("gui/datachest/datachest-combat.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        combatDataGUI = ChestGui.load(this, xmlStream);
        combatDataGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    public void open(Player player) {
        combatDataGUI.show(player);
    }

    public void toDataChest(InventoryClickEvent event) {
        globalGuiController.openDataChest(event);
    }

    public void cancelClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
