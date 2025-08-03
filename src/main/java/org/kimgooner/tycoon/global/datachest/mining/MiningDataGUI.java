package org.kimgooner.tycoon.global.datachest.mining;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.kimgooner.tycoon.global.template.DataChestTemplate;

public class MiningDataGUI {
    Inventory miningDataGUI = DataChestTemplate.createEmptyFrame("데이터 보관함 - 채광");

    public void open(Player player){
        player.openInventory(miningDataGUI);
    }
}
