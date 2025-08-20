package org.kimgooner.tycoon;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.discord.DiscordWebhookListener;
import org.kimgooner.tycoon.discord.DiscordWebhookSender;
import org.kimgooner.tycoon.global.gui.menu.MenuItemUtil;
import org.kimgooner.tycoon.util.EnvLoader;

@Getter
public final class Tycoon extends JavaPlugin {
    private static HikariDataSource dataSource;
    private GlobalController globalController;

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

                globalController = new GlobalController(this, databaseManager);

                // 나머지 초기화 코드
                MenuItemUtil.init(this);

                DiscordWebhookSender sender = new DiscordWebhookSender(this);
                getServer().getPluginManager().registerEvents(sender, this);
                getServer().getPluginManager().registerEvents(new DiscordWebhookListener(sender), this);

                for(Player player : Bukkit.getOnlinePlayers()){
                    initTable(player);
                    globalController.getInitializedPlayers().add(player.getUniqueId());
                    getLogger().info(player.getName() + " 초기 세팅 완료.");
                }
            });
        });
    }

    private void initTable(Player player) {
        GlobalDAOController globalDAOController = globalController.getGlobalDaoController();
        getLogger().info(player.getName() + "접속, DB 확인.");
        getLogger().info("-----------------------------");
        if(!globalDAOController.getMemberDAO().hasData(player)) {
            globalDAOController.getMemberDAO().init(player);
            getLogger().info(player.getName() + "의 멤버 DB 생성");
        }
        // 채광
        if(!globalDAOController.getMiningDAO().hasData(player)) {
            globalDAOController.getMiningDAO().init(player);
            getLogger().info(player.getName() + "의 채광 DB 생성");
        }
        if(!globalDAOController.getHeartDAO().hasData(player)) {
            globalDAOController.getHeartDAO().init(player);
            getLogger().info(player.getName() + "의 동굴의 심장 DB 생성");
        }
        if(!globalDAOController.getHeartInfoDAO().hasData(player)) {
            globalDAOController.getHeartInfoDAO().init(player);
            getLogger().info(player.getName() + "의 동굴의 심장 정보 DB 생성");
        }

        // 농사
        if(!globalDAOController.getFarmingDAO().hasData(player)) {
            globalDAOController.getFarmingDAO().init(player);
            getLogger().info(player.getName() + "의 농사 DB 생성");
        }

        // 낚시
        if(!globalDAOController.getFishingDAO().hasData(player)) {
            globalDAOController.getFishingDAO().init(player);
            getLogger().info(player.getName() + "의 낚시 DB 생성");
        }

        // 전투
        if(!globalDAOController.getCombatDAO().hasData(player)) {
            globalDAOController.getCombatDAO().init(player);
            getLogger().info(player.getName() + "의 전투 DB 생성");
        }

        // 데이터 보관함
        for (int categoryId = 1; categoryId <= 4; categoryId++) {
            if(!globalDAOController.getDataStorageDAO().hasData(player, categoryId)) {
                globalDAOController.getDataStorageDAO().init(player, categoryId);
                getLogger().info(player.getName() + "의 데이터 보관함 DB 생성, type: " + categoryId);
            }
        }
        getLogger().info("-----------------------------");
        getLogger().info(player.getName() + "의 DB 확인 완료.");
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인 종료 테스트");
        dataSource.close(); // HikariCP 종료
    }
}
