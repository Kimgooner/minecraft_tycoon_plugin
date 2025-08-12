package org.kimgooner.tycoon;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.discord.DiscordWebhookListener;
import org.kimgooner.tycoon.discord.DiscordWebhookSender;
import org.kimgooner.tycoon.global.gui.menu.MenuItemUtil;
import org.kimgooner.tycoon.global.item.job.mining.PickaxeList;
import org.kimgooner.tycoon.util.EnvLoader;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Tycoon extends JavaPlugin {
    private static HikariDataSource dataSource;
    private final Set<UUID> editModePlayers = new HashSet<>();

    public Set<UUID> getEditModePlayers() {
        return editModePlayers;
    }

    @Override
    public void onEnable() {
        getLogger().info("플러그인 시작 테스트");
        //DB 연결
        //HikariCP 설정
        EnvLoader loader = new EnvLoader(this);
        String DB_URL = loader.getDBUrl();
        String USER_NAME = loader.getUSERNAME();
        String PASSWORD = loader.getUSERPASSWORD();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER_NAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(15); // 최대 연결 수
        config.setMinimumIdle(1);      // 최소 유휴 연결 수 유지
        config.setConnectionTimeout(30000); // 연결 대기 시간 (ms)
        config.setIdleTimeout(300000);       // 유휴 연결 유지 시간 (ms)
        config.setMaxLifetime(1800000);      // 커넥션 최대 수명 (ms)

        dataSource = new HikariDataSource(config);
        DatabaseManager databaseManager = new DatabaseManager(dataSource);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            databaseManager.init(this);
            Bukkit.getScheduler().runTask(this, () -> {
                getLogger().info("Database initialized!");

                GlobalController globalController = new GlobalController(this, databaseManager);

                // 나머지 초기화 코드
                MenuItemUtil.init(this);
                PickaxeList pickaxeList = new PickaxeList(this);

                DiscordWebhookSender sender = new DiscordWebhookSender(this);
                getServer().getPluginManager().registerEvents(sender, this);
                getServer().getPluginManager().registerEvents(new DiscordWebhookListener(sender), this);
            });
        });
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인 종료 테스트");
        dataSource.close(); // HikariCP 종료
    }
}
