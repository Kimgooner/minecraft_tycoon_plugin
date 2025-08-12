package org.kimgooner.tycoon.db.dao.job.mining;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.discord.DiscordWebhookLevelUpEvent;
import org.kimgooner.tycoon.discord.LevelUpType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class HeartInfoDAO {
    private final DatabaseManager databaseManager;
    private final JavaPlugin plugin;

    public HeartInfoDAO(DatabaseManager databaseManager, JavaPlugin plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
    }

    private final Map<Integer, Integer> HEART_KEYS = Map.of(
            1, 1,
            2, 2,
            3, 2,
            4, 2,
            5, 2,
            6, 2,
            7, 3,
            8, 3,
            9, 3
    );

    public void sendHeartLevelUpMessage(Player player, Integer level) {
        player.sendMessage(Component.text("§f[시스템]: §5동굴의 심장 §f등급이 상승했습니다! §84 §f-> §55"));
        player.sendMessage(Component.text("§f[시스템]: §f등급 달성 보너스:"));
        player.sendMessage(Component.text("§f[시스템]:  §8+§5%d 심장 해방의 열쇠".formatted(HEART_KEYS.getOrDefault(level, 0))));

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
    }

    public void init(Player player) {
        String sql = "INSERT IGNORE INTO cave_heart_info (member_uuid, level, heart_key, low_powder, high_powder, used_heart_key, used_low_powder, used_high_powder) VALUES (?, 0, 0, 0, 0, 0, 0, 0)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM cave_heart_info WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 공통 get 메서드 패턴
    private Integer getIntField(Player player, String column) {
        String sql = "SELECT " + column + " FROM cave_heart_info WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getLevel(Player player) { return getIntField(player, "level"); }
    public Integer getHeartKey(Player player) { return getIntField(player, "heart_key"); }
    public Integer getLowPowder(Player player) { return getIntField(player, "low_powder"); }
    public Integer getHighPowder(Player player) { return getIntField(player, "high_powder"); }
    public Integer getUsedHeartKey(Player player) { return getIntField(player, "used_heart_key"); }
    public Integer getUsedLowPowder(Player player) { return getIntField(player, "used_low_powder"); }
    public Integer getUsedHighPowder(Player player) { return getIntField(player, "used_high_powder"); }

    // 공통 add 메서드 패턴
    private void addValue(Player player, String column, int value) {
        String sql = "UPDATE cave_heart_info SET " + column + " = " + column + " + ? WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLevel(Player player) {
        String sql = "UPDATE cave_heart_info SET level = level + 1 WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            Integer heartLevel = getLevel(player);
            DiscordWebhookLevelUpEvent event = new DiscordWebhookLevelUpEvent(player, LevelUpType.HEART, heartLevel);
            sendHeartLevelUpMessage(player, heartLevel);
            plugin.getServer().getPluginManager().callEvent(event);
            addHeartKey(player, HEART_KEYS.getOrDefault(heartLevel, 0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addHeartKey(Player player, int value) { addValue(player, "heart_key", value); }
    public void addLowPowder(Player player, int value) { addValue(player, "low_powder", value); }
    public void addHighPowder(Player player, int value) { addValue(player, "high_powder", value); }
    public void addUsedHeartKey(Player player, int value) { addValue(player, "used_heart_key", value); }
    public void addUsedLowPowder(Player player, int value) { addValue(player, "used_low_powder", value); }
    public void addUsedHighPowder(Player player, int value) { addValue(player, "used_high_powder", value); }

    // remove 메서드는 update 문에서 newValue 바인딩 누락, 그리고 sql 문법도 틀림 -> 수정했습니다.
    private boolean removeValue(Player player, String column, int value, Runnable addUsedFunc) {
        int current = getIntField(player, column);
        int newValue = current - value;
        if (newValue < 0) return false;

        String sql = "UPDATE cave_heart_info SET " + column + " = ? WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newValue);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            addUsedFunc.run();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeHeartKey(Player player, int value) {
        return removeValue(player, "heart_key", value, () -> addUsedHeartKey(player, value));
    }
    public boolean removeLowPowder(Player player, int value) {
        return removeValue(player, "low_powder", value, () -> addUsedLowPowder(player, value));
    }
    public boolean removeHighPowder(Player player, int value) {
        return removeValue(player, "high_powder", value, () -> addUsedHighPowder(player, value));
    }

    public void resetUsed(Player player) {
        int heartKey = getUsedHeartKey(player);
        int lowPowder = getUsedLowPowder(player);
        int highPowder = getUsedHighPowder(player);

        String sql = "UPDATE cave_heart_info SET used_heart_key = 0, used_low_powder = 0, used_high_powder = 0 WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            addHeartKey(player, heartKey);
            addLowPowder(player, lowPowder);
            addHighPowder(player, highPowder);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
