package org.kimgooner.tycoon.global.gui.datachest.gui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.global.global.SoundUtil;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.gui.datachest.util.DataChestUtil;

import java.io.InputStream;
import java.util.List;

public class MiningDataGUI {
    private final DataStorageDAO dataStorageDAO;
    private final GlobalGUIController globalGuiController;
    private final SoundUtil soundUtil =  new SoundUtil();

    private final ChestGui miningDataGUI;

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

    List<Integer> gradeList = List.of(
            0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8
    );

    List<String> itemNameList = List.of(
            "돌",
            "석탄",
            "구리",
            "철",
            "금",
            "레드스톤",
            "청금석",
            "에메랄드",
            "다이아몬드"
    );

    public MiningDataGUI(JavaPlugin plugin, DataStorageDAO dataStorageDAO, GlobalGUIController globalGuiController) {
        this.dataStorageDAO = dataStorageDAO;
        this.globalGuiController = globalGuiController;

        InputStream xmlStream = plugin.getResource("gui/datachest/datachest-mining.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        miningDataGUI = ChestGui.load(this, xmlStream);
        miningDataGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    public void open(Player player) {
        ChestGui playerGUI = miningDataGUI.copy();
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
        playerGUI.show(player);
        DataChestUtil.populateItems(playerGUI, itemList, gradeList, itemNameList, amountList);
    }

    public void toDataChest(InventoryClickEvent event) {
        globalGuiController.openDataChest(event);
    }
}
