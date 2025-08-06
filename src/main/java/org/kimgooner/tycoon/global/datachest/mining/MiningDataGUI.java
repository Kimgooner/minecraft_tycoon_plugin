package org.kimgooner.tycoon.global.datachest.mining;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.global.template.DataChestTemplate;

import java.util.List;

public class MiningDataGUI {
    private final DataStorageDAO dataStorageDAO;
    Inventory miningDataGUI = DataChestTemplate.createEmptyFrame("데이터 보관함 - 채광");

    public MiningDataGUI(DataStorageDAO dataStorageDAO) {
        this.dataStorageDAO = dataStorageDAO;
    }

    List<ItemStack> itemList = List.of(
            new ItemStack(Material.STONE),
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
        List<DataStorageDAO.DataStorage> miningDataStorages = dataStorageDAO.getDataStorageByCategory(player, 1);
        List<Integer> amountList = List.of(
                miningDataStorages.get(0).getAmount(),
                miningDataStorages.get(1).getAmount(),
                miningDataStorages.get(2).getAmount(),
                miningDataStorages.get(3).getAmount(),
                miningDataStorages.get(4).getAmount(),
                miningDataStorages.get(5).getAmount(),
                miningDataStorages.get(6).getAmount(),
                miningDataStorages.get(7).getAmount(),
                miningDataStorages.get(8).getAmount()
        );
        List<Integer> gradeList = List.of(
                0,1,1,2,2,3,3,4,4
        );
        DataChestTemplate.populateItems(miningDataGUI, itemList, amountList, gradeList);
        player.openInventory(miningDataGUI);
    }
}
