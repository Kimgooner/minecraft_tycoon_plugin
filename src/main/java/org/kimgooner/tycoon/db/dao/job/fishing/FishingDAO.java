package org.kimgooner.tycoon.db.dao.job.fishing;

import org.bukkit.entity.Player;
import org.kimgooner.tycoon.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FishingDAO {
    private final DatabaseManager databaseManager;

    public FishingDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public static class FishingStats {
        int level;
        int exp;
        int wisdom;
        int speed;
        int multihook;
        int wonder;
        public FishingStats(int level, int exp, int wisdom, int speed, int multihook, int wonder) {
            this.level = level;
            this.exp = exp;
            this.wisdom = wisdom;
            this.speed = speed;
            this.multihook = multihook;
            this.wonder = wonder;
        }
        public int getLevel() {return level;}
        public int getExp() {return exp;}
        public int getWisdom() {return wisdom;}
        public int getSpeed() {return speed;}
        public int getMultihook() {return multihook;}
        public int getWonder() {return wonder;}
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM fishings WHERE member_uuid = ?";
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
        String sql = "INSERT INTO fishings (member_uuid, level, exp, wisdom, speed, multihook, wonder) VALUES (?, 1, 0, 0, 0, 0, 0)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FishingStats getFishingStats(Player player) {
        String sql = "SELECT level, exp, wisdom, speed, multihook, wonder FROM fishings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FishingStats(
                            rs.getInt("level"),
                            rs.getInt("exp"),
                            rs.getInt("wisdom"),
                            rs.getInt("speed"),
                            rs.getInt("multihook"),
                            rs.getInt("wonder")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new FishingStats(1, 0, 0, 0, 0, 0);
    }

    public void setStat(Player player, String tag, int amount) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "speed", "multihook", "wonder");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE fishings SET " + tag + " = ? WHERE member_uuid = ?";
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
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "speed", "multihook", "wonder");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "SELECT " + tag + " FROM fishings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(tag);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}