package org.kimgooner.tycoon.db.dao.job.mining;

import org.bukkit.entity.Player;
import org.kimgooner.tycoon.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HeartDAO {
    private final DatabaseManager databaseManager;

    public HeartDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Integer getLevel(Player player, Integer index) {
        String sql = "SELECT value FROM cave_heart WHERE member_uuid = ? AND slot_index = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, index);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setStat(Player player, Integer index, Integer value) {
        String sql = "UPDATE cave_heart SET value = ? WHERE member_uuid = ? AND slot_index = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.setInt(3, index);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLevel(Player player, Integer index) {
        String sql = "UPDATE cave_heart SET value = value + 1 WHERE member_uuid = ? AND slot_index = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, index);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init(Player player) {
        String sql = "INSERT IGNORE INTO cave_heart (member_uuid, slot_index, value) VALUES (?, ?, 0)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 33; i++) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setInt(2, i);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT COUNT(*) FROM cave_heart WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) >= 33;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<Integer, Integer> getAllLevels(Player player) {
        String sql = "SELECT slot_index, value FROM cave_heart WHERE member_uuid = ?";
        Map<Integer, Integer> levelMap = new HashMap<>();
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    levelMap.put(rs.getInt("slot_index"), rs.getInt("value"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return levelMap;
    }
}
