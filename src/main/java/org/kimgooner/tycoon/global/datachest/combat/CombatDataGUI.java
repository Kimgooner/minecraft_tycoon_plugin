package org.kimgooner.tycoon.global.datachest.combat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.global.template.DataChestTemplate;

import java.util.List;

public class CombatDataGUI {
    private final DataStorageDAO dataStorageDAO;
    Inventory combatDataGUI = DataChestTemplate.createEmptyFrame("데이터 보관함 - 전투");

    public CombatDataGUI(DataStorageDAO dataStorageDAO) {
        this.dataStorageDAO = dataStorageDAO;
    }

    List<ItemStack> itemList = List.of(
            new ItemStack(Material.COAL),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.REDSTONE),
            new ItemStack(Material.LAPIS_LAZULI),
            new ItemStack(Material.EMERALD),
            new ItemStack(Material.DIAMOND)
    );

    public void open(Player player){

        List<Integer> amountList = List.of(1,1,1,1,1,1,1,1);


        player.openInventory(combatDataGUI);
    }
}
