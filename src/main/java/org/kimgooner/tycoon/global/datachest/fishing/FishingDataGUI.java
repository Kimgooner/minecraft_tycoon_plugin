package org.kimgooner.tycoon.global.datachest.fishing;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.kimgooner.tycoon.global.template.DataChestTemplate;

public class FishingDataGUI {
    Inventory fishingDataGUI = DataChestTemplate.createEmptyFrame("데이터 보관함 - 낚시");

    public void open(Player player){
        player.openInventory(fishingDataGUI);
    }
}
