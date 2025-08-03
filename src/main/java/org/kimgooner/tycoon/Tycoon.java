package org.kimgooner.tycoon;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.db.dao.*;
import org.kimgooner.tycoon.global.datachest.DataChestEventHandler;
import org.kimgooner.tycoon.global.datachest.DataChestGUI;
import org.kimgooner.tycoon.global.datachest.combat.CombatDataEventHandler;
import org.kimgooner.tycoon.global.datachest.combat.CombatDataGUI;
import org.kimgooner.tycoon.global.datachest.farming.FarmingDataEventHandler;
import org.kimgooner.tycoon.global.datachest.farming.FarmingDataGUI;
import org.kimgooner.tycoon.global.datachest.fishing.FishingDataEventHandler;
import org.kimgooner.tycoon.global.datachest.fishing.FishingDataGUI;
import org.kimgooner.tycoon.global.datachest.mining.MiningDataEventHandler;
import org.kimgooner.tycoon.global.datachest.mining.MiningDataGUI;
import org.kimgooner.tycoon.global.global.GlobalEventHandler;
import org.kimgooner.tycoon.global.menu.MenuEventHandler;
import org.kimgooner.tycoon.global.menu.MenuGUI;
import org.kimgooner.tycoon.global.menu.MenuItemUtil;
import org.kimgooner.tycoon.job.mining.MiningCommandHandler;
import org.kimgooner.tycoon.job.mining.MiningEventHandler;

import java.sql.Connection;

public final class Tycoon extends JavaPlugin {
    private Connection connection;

    @Override
    public void onEnable() {
        getLogger().info("플러그인 시작 테스트");
        //DB 연결
        DatabaseManager.init();

        //DAO 연결
        MemberDAO memberDAO = new MemberDAO(DatabaseManager.getConnection());
        MiningDAO miningDAO = new MiningDAO(DatabaseManager.getConnection());
        FarmingDAO farmingDAO = new FarmingDAO(DatabaseManager.getConnection());
        FishingDAO fishingDAO = new FishingDAO(DatabaseManager.getConnection());
        CombatDAO combatDAO = new CombatDAO(DatabaseManager.getConnection());

        //GUI 및 기타 아이템 연결
        MenuItemUtil.init(this);
        MenuGUI menuGUI = new MenuGUI(memberDAO, miningDAO, farmingDAO, fishingDAO, combatDAO);

        CombatDataGUI combatDataGUI = new CombatDataGUI();
        FarmingDataGUI farmingDataGUI = new FarmingDataGUI();
        FishingDataGUI fishingDataGUI = new FishingDataGUI();
        MiningDataGUI miningDataGUI = new MiningDataGUI();
        DataChestGUI dataChestGUI = new DataChestGUI();

        //메뉴 핸들러
        getServer().getPluginManager().registerEvents(new MenuEventHandler(menuGUI, dataChestGUI), this);

        //데이터 보관함 핸들러
        getServer().getPluginManager().registerEvents(new DataChestEventHandler(menuGUI, miningDataGUI, farmingDataGUI, fishingDataGUI, combatDataGUI), this);

        //데이터 보관함 카테고리 별 핸들러
        getServer().getPluginManager().registerEvents(new MiningDataEventHandler(dataChestGUI), this);
        getServer().getPluginManager().registerEvents(new FarmingDataEventHandler(dataChestGUI), this);
        getServer().getPluginManager().registerEvents(new FishingDataEventHandler(dataChestGUI), this);
        getServer().getPluginManager().registerEvents(new CombatDataEventHandler(dataChestGUI), this);

        //전역 이벤트 핸들러
        getServer().getPluginManager().registerEvents(new GlobalEventHandler(this), this);

        //채광 이벤트 핸들러
        getServer().getPluginManager().registerEvents(new MiningEventHandler(miningDAO, this), this);

        //채광 명령어 핸들러
        getCommand("mining").setExecutor(new MiningCommandHandler(miningDAO));



    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인 종료 테스트");
        // Plugin shutdown logic
    }
}
