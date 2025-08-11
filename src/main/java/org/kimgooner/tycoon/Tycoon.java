package org.kimgooner.tycoon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.discord.DiscordWebhookListener;
import org.kimgooner.tycoon.discord.DiscordWebhookSender;
import org.kimgooner.tycoon.global.gui.menu.MenuItemUtil;
import org.kimgooner.tycoon.global.item.job.mining.PickaxeList;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Tycoon extends JavaPlugin {
    private Connection connection;
    private final Set<UUID> editModePlayers = new HashSet<>();

    public Set<UUID> getEditModePlayers() {
        return editModePlayers;
    }

    @Override
    public void onEnable() {
        getLogger().info("플러그인 시작 테스트");
        //DB 연결
        DatabaseManager.init(this);
        GlobalController globalController = new GlobalController(this, DatabaseManager.getConnection());

        //기타
        MenuItemUtil.init(this);
        PickaxeList pickaxeList = new PickaxeList(this);

        //DiscordWebhook
        getServer().getPluginManager().registerEvents(new DiscordWebhookSender(this), this);
        getServer().getPluginManager().registerEvents(new DiscordWebhookListener(new DiscordWebhookSender(this)), this);

        for (Player player : getServer().getOnlinePlayers()) {
            getLogger().info("현재 접속 중인 플레이어: " + player.getName());
            // 필요하면 여기서 플레이어별 초기화 작업 수행 가능
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("플러그인 종료 테스트");
        // Plugin shutdown logic
    }
}
