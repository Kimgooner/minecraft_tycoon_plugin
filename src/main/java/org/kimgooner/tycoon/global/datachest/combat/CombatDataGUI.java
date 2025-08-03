package org.kimgooner.tycoon.global.datachest.combat;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.kimgooner.tycoon.global.template.DataChestTemplate;

public class CombatDataGUI {
    Inventory combatDataGUI = DataChestTemplate.createEmptyFrame("데이터 보관함 - 전투");

    public void open(Player player){
        player.openInventory(combatDataGUI);
    }
}
