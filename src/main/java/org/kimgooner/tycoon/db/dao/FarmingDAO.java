package org.kimgooner.tycoon.db.dao;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FarmingDAO {
    private final Connection conn;

    public FarmingDAO(Connection conn) {
        this.conn = conn;
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
        public int getLevel() {
            return level;
        }
        public int getExp() {
            return exp;
        }
        public int getWisdom() {
            return wisdom;
        }
        public int getFortune() {
            return fortune;
        }
        public int getRichness() {
            return richness;
        }
    }

    public FarmingStats getFarmingStats(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM farmings WHERE member_uuid = ?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new FarmingStats(
                        rs.getInt("level"),
                        rs.getInt("exp"),
                        rs.getInt("wisdom"),
                        rs.getInt("fortune"),
                        rs.getInt("richness"));
            } else {
                PreparedStatement insert = conn.prepareStatement("INSERT INTO farmings (member_uuid, level, exp, wisdom, fortune, richness) VALUES (?, 1, 0, 0, 0, 0)");
                insert.setString(1, player.getUniqueId().toString());
                insert.executeUpdate();
                return new FarmingStats(1,0,0,0,0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new FarmingStats(1,0,0,0,0);
        }
    }

    public void setStat(Player player, String tag, int amount) {
        // 안전을 위해 컬럼명 검증 (SQL Injection 방지)
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "speed", "richness");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE farmings SET " + tag + " = ? WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStat(Player player, String tag) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT ? FROM farmings WHERE member_uuid = ?");
            ps.setString(1, tag);
            ps.setString(2, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(tag);
        } catch (SQLException e) {}
        return 0;
    }
}