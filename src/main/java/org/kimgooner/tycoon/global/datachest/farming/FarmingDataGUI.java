package org.kimgooner.tycoon.global.datachest.farming;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.kimgooner.tycoon.global.template.DataChestTemplate;

public class FarmingDataGUI {
    Inventory farmingDataGUI = DataChestTemplate.createEmptyFrame("데이터 보관함 - 농사");

    public void open(Player player){
        player.openInventory(farmingDataGUI);
    }
}
