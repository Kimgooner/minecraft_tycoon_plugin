package org.kimgooner.tycoon.db.dao.mining;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MiningDAO {
    private final Connection conn;

    public MiningDAO(Connection conn) {
        this.conn = conn;
    }

    public static class MiningStats {
        int level;
        int exp;
        int wisdom;
        int fortune;
        int speed;
        int pristine;
        int power;
        int light;
        int spread;
        public MiningStats(int level, int exp, int wisdom, int fortune, int speed, int pristine, int power, int light, int spread) {
            this.level = level;
            this.exp = exp;
            this.wisdom = wisdom;
            this.fortune = fortune;
            this.speed = speed;
            this.pristine = pristine;
            this.power = power;
            this.light = light;
            this.spread = spread;
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
        public int getSpeed() {
            return speed;
        }
        public int getPristine() {
            return pristine;
        }
        public int getPower() {
            return power;
        }
        public int getLight() {
            return light;
        }
        public int getSpread() {
            return spread;
        }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM minings WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        if (hasData(player)) return; // 이미 있으면 무시
        String sql = "INSERT INTO minings (member_uuid, level, exp, wisdom, fortune, speed, pristine, power, light, spread) " +
                "VALUES (?, 1, 0, 0, 0, 0, 0, 0, 0, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MiningDAO.MiningStats getMiningStats(Player player) {
        String sql = "SELECT level, exp, wisdom, fortune, speed, pristine, power, light, spread FROM minings WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MiningDAO.MiningStats(
                            rs.getInt("level"),
                            rs.getInt("exp"),
                            rs.getInt("wisdom"),
                            rs.getInt("fortune"),
                            rs.getInt("speed"),
                            rs.getInt("pristine"),
                            rs.getInt("power"),
                            rs.getInt("light"),
                            rs.getInt("spread")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 기본값 반환 (DB 삽입은 init()에서만 함)
        return new MiningDAO.MiningStats(1, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public void setStat(Player player, String tag, int amount) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "speed", "pristine", "power", "light", "spread");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE minings SET " + tag + " = ? WHERE member_uuid = ?";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStat(Player player, String tag) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "speed", "pristine", "power", "light", "spread");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "SELECT " + tag + " FROM minings WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
