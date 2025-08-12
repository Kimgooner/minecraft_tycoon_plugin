package org.kimgooner.tycoon.db.dao.job.farming;

import org.bukkit.entity.Player;
import org.kimgooner.tycoon.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FarmingDAO {
    private final DatabaseManager databaseManager;

    public FarmingDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static class FarmingStats {
        int level;
        int exp;
        int wisdom;
        int fortune;
        int richness;

        public FarmingStats(int level, int exp, int wisdom, int fortune, int richness) {
            this.level = level;
            this.exp = exp;
            this.wisdom = wisdom;
            this.fortune = fortune;
            this.richness = richness;
        }

        public int getLevel() { return level; }
        public int getExp() { return exp; }
        public int getWisdom() { return wisdom; }
        public int getFortune() { return fortune; }
        public int getRichness() { return richness; }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM farmings WHERE member_uuid = ?";
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

    public void init(Player player) {
        if (hasData(player)) return;
        String sql = "INSERT INTO farmings (member_uuid, level, exp, wisdom, fortune, richness) VALUES (?, 1, 0, 0, 0, 0)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FarmingStats getFarmingStats(Player player) {
        String sql = "SELECT level, exp, wisdom, fortune, richness FROM farmings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FarmingStats(
                            rs.getInt("level"),
                            rs.getInt("exp"),
                            rs.getInt("wisdom"),
                            rs.getInt("fortune"),
                            rs.getInt("richness")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new FarmingStats(1, 0, 0, 0, 0);
    }

    public void setStat(Player player, String tag, int amount) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "richness");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE farmings SET " + tag + " = ? WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStat(Player player, String tag) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "richness");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "SELECT " + tag + " FROM farmings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}