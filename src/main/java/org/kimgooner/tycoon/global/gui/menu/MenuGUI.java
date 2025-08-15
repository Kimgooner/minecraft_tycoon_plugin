package org.kimgooner.tycoon.global.gui.menu;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.db.dao.MemberDAO;
import org.kimgooner.tycoon.db.dao.job.combat.CombatDAO;
import org.kimgooner.tycoon.db.dao.job.farming.FarmingDAO;
import org.kimgooner.tycoon.db.dao.job.fishing.FishingDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.job.mining.controller.MiningController;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuGUI {
    private final GlobalGUIController globalGUIController;

    private final ChestGui menuGUI;
    private final MemberDAO memberDAO;
    private final MiningDAO miningDAO;
    private final FarmingDAO farmingDAO;
    private final FishingDAO fishingDAO;
    private final CombatDAO combatDAO;

    private final MiningController miningController;
    private final Map<UUID, MiningStat> miningStatMap;

    public MenuGUI(JavaPlugin plugin, GlobalGUIController globalGUIController, GlobalDAOController globalDAOController, MiningController miningController) {
        this.globalGUIController = globalGUIController;


        this.memberDAO = globalDAOController.getMemberDAO();
        this.miningDAO = globalDAOController.getMiningDAO();
        this.farmingDAO = globalDAOController.getFarmingDAO();
        this.fishingDAO = globalDAOController.getFishingDAO();
        this.combatDAO = globalDAOController.getCombatDAO();

        this.miningController = miningController;
        this.miningStatMap = miningController.getMiningMap();
        InputStream xmlStream = plugin.getResource("gui/menu/menu.xml");
        if (xmlStream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다.");
        }
        this.menuGUI = ChestGui.load(this, xmlStream);
        this.menuGUI.setOnGlobalClick(event -> event.setCancelled(true));
    }

    private final List<Double> EXP_LISTS = List.of(
            50.0, 125.0, 200.0, 300.0, 500.0, 750.0, 1000.0, 1500.0, 2000.0,
            3500.0, 5000.0, 7500.0, 10000.0, 15000.0, 20000.0, 30000.0, 50000.0, 75000.0, 100000.0,
            200000.0, 300000.0, 400000.0, 500000.0, 600000.0, 700000.0, 800000.0, 900000.0, 1000000.0, 1100000.0, 1200000.0,
            1300000.0, 1400000.0, 1500000.0, 1600000.0, 1700000.0, 1800000.0, 1900000.0, 2000000.0, 2100000.0, 2200000.0,
            2300000.0, 2400000.0, 2500000.0, 2600000.0, 2750000.0, 2900000.0, 3100000.0, 3400000.0, 3700000.0, 4000000.0,
            4300000.0, 4600000.0, 4900000.0, 5200000.0, 5500000.0, 5800000.0, 6100000.0, 6400000.0, 6700000.0, 7000000.0
    );

    public void open(Player player) {
        ChestGui playerMenu = menuGUI.copy();

        MiningStat miningOverall = miningController.getMiningStat(player);
        int miningLevel = miningDAO.getLevel(player);
        double miningExp = miningDAO.getExp(player);;

        FarmingDAO.FarmingStats farmingStats = farmingDAO.getFarmingStats(player);
        FishingDAO.FishingStats fishingStats = fishingDAO.getFishingStats(player);
        CombatDAO.CombatStats combatStats = combatDAO.getCombatStats(player);

        ItemStack statMining = new ItemBuilder(Material.STONE_PICKAXE)
                .hideAttributeModifiers()
                .displayName(Component.text("§f채광 스텟"))
                .addLore(Component.text("§7숙련도:"))
                .addLore(Component.text(String.format(" §f레벨: %,d", miningLevel)))
                .addLore(Component.text(String.format(" §f경험치: %,.1f", miningExp)))
                .addLore(Component.text(" " + makePercentBar(miningExp, EXP_LISTS.get(miningLevel))))
                .addLore(Component.text(" §f채광 경험치 획득량:"))
                .addLore(Component.text(" §7레벨 보너스:"))
                .addLore(Component.text(String.format("  §f채광 속도: §6+%,d ☘", miningLevel * 4)))
                .addLore(Component.text("§7스텟:"))
                .addLore(Component.text(String.format(" §f채광 속도: §6%,d ⸕", miningOverall.getSpeed())))
                .addLore(Component.text(String.format(" §f채광 행운: §6%,d ☘", miningOverall.getFortune())))
                .addLore(Component.text(String.format(" §f연쇄 파괴: §e%,d ▚", miningOverall.getSpread())))
                .addLore(Component.text(String.format(" §f빛: §e%,d ✦", miningOverall.getLight())))
                .build();
        ItemStack statFarming = new ItemBuilder(Material.DIAMOND_HOE)
                .hideAttributeModifiers()
                .displayName(Component.text("§f농사 스텟"))
                .addLore(Component.text("§7숙련도:"))
                .addLore(Component.text(" §f레벨:"))
                .addLore(Component.text(" §f경험치:"))
                .addLore(Component.text(" " + makePercentBar(43.0, 100.0)))
                .addLore(Component.text(" §7레벨 보너스:"))
                .addLore(Component.text("  §f채광 속도: §6+5 ⸕"))
                .addLore(Component.text("  §f채광 행운: §6+4 ☘"))
                .addLore(Component.text("§7스텟:"))
                .addLore(Component.text(" §f채광 속도:"))
                .addLore(Component.text(" §f채광 행운:"))
                .addLore(Component.text(" §f연쇄 파괴:"))
                .addLore(Component.text(" §f빛:"))
                .addLore(Component.text(" §f순수:"))
                .addLore(Component.text("§7특수 스텟:"))
                .addLore(Component.text(" §f광산 광물 재생 속도:"))
                .addLore(Component.text(" §f광물을 발견할 확률:"))
                .addLore(Component.text(" §f풍부한 광물을 발견할 확률:"))
                .addLore(Component.text(" §f상자를 발견할 확률:"))
                .addLore(Component.text(" §f상위 등급 상자를 발견할 확률:"))
                .addLore(Component.text(" §f균열 광물을 발견할 확률:"))
                .addLore(Component.text(" §f움브랄나이트를 발견할 확률:"))
                .addLore(Component.text(" "))
                .addLore(Component.text(" §f채광 경험치 획득량:"))
                .addLore(Component.text(" "))
                .addLore(Component.text(" §f광석 가루 획득량:"))
                .addLore(Component.text(" §f하위 광석 가루 추가:"))
                .addLore(Component.text(" §f상위 광석 가루 추가:"))

                .build();
        ItemStack statPlayer = new ItemBuilder(Material.SUNFLOWER)
                .displayName(Component.text("§f플레이어 스텟"))
                .build();
        ItemStack statFishing = new ItemBuilder(Material.FISHING_ROD)
                .hideAttributeModifiers()
                .displayName(Component.text("§f낚시 스텟"))
                .addLore(Component.text("§7숙련도:"))
                .addLore(Component.text(" §f레벨:"))
                .addLore(Component.text(" §f경험치:"))
                .addLore(Component.text(" " + makePercentBar(43.0, 100.0)))
                .addLore(Component.text(" §7레벨 보너스:"))
                .addLore(Component.text("  §f채광 속도: §6+5 ⸕"))
                .addLore(Component.text("  §f채광 행운: §6+4 ☘"))
                .addLore(Component.text("§7스텟:"))
                .addLore(Component.text(" §f채광 속도:"))
                .addLore(Component.text(" §f채광 행운:"))
                .addLore(Component.text(" §f연쇄 파괴:"))
                .addLore(Component.text(" §f빛:"))
                .addLore(Component.text(" §f순수:"))
                .addLore(Component.text("§7특수 스텟:"))
                .addLore(Component.text(" §f채광 경험치 획득량:"))
                .addLore(Component.text(" §f광석 가루 획득량:"))
                .addLore(Component.text(" §f광물을 발견할 확률:"))
                .addLore(Component.text(" §f풍부한 광물을 발견할 확률:"))
                .build();
        ItemStack statCombat = new ItemBuilder(Material.IRON_SWORD)
                .hideAttributeModifiers()
                .displayName(Component.text("§f전투 스텟"))
                .addLore(Component.text("§7숙련도:"))
                .addLore(Component.text(" §f레벨:"))
                .addLore(Component.text(" §f경험치:"))
                .addLore(Component.text(" " + makePercentBar(43.0, 100.0)))
                .addLore(Component.text(" §7레벨 보너스:"))
                .addLore(Component.text("  §f채광 속도: §6+5 ⸕"))
                .addLore(Component.text("  §f채광 행운: §6+4 ☘"))
                .addLore(Component.text("§7스텟:"))
                .addLore(Component.text(" §f채광 속도:"))
                .addLore(Component.text(" §f채광 행운:"))
                .addLore(Component.text(" §f연쇄 파괴:"))
                .addLore(Component.text(" §f빛:"))
                .addLore(Component.text(" §f순수:"))
                .addLore(Component.text("§7특수 스텟:"))
                .addLore(Component.text(" §f채광 경험치 획득량:"))
                .addLore(Component.text(" §f광석 가루 획득량:"))
                .addLore(Component.text(" §f광물을 발견할 확률:"))
                .addLore(Component.text(" §f풍부한 광물을 발견할 확률:"))
                .build();
        playerMenu.show(player);

        playerMenu.getInventory().setItem(19, statMining);
        playerMenu.getInventory().setItem(20, statFarming);
        playerMenu.getInventory().setItem(22, statPlayer);
        playerMenu.getInventory().setItem(24, statFishing);
        playerMenu.getInventory().setItem(25, statCombat);
    }

    public String makePercentBar(Double cur, Double max) {
        if (max == 0) return "§f□□□□□□□□□□"; // 0으로 나누는 상황 방지

        double percent = (double) cur / max;
        int yellowBars = (int) Math.floor(percent * 10); // 0~10칸

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < yellowBars) {
                bar.append("§e■"); // 노란색
            } else {
                bar.append("§f■"); // 흰색
            }
        }
        return bar.toString();
    }

    public void toDataChest(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        globalGUIController.openDataChest(event);
    }

    public void toClose(InventoryClickEvent event) {
        globalGUIController.closeInventory(event);
    }
}
