package org.kimgooner.tycoon;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.db.dao.*;
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

        getServer().getPluginManager().registerEvents(new MiningEventHandler(miningDAO, this), this);
        getServer().getPluginManager().registerEvents(new MenuEventHandler(menuGUI), this);
        getServer().getPluginManager().registerEvents(new GlobalEventHandler(this), this);
        getCommand("mining").setExecutor(new MiningCommandHandler(miningDAO));

    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인 종료 테스트");
        // Plugin shutdown logic
    }
}
