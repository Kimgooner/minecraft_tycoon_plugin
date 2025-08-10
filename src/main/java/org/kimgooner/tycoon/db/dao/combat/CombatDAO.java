package org.kimgooner.tycoon.db.dao.combat;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CombatDAO {
    private final Connection conn;

    public CombatDAO(Connection conn) {
        this.conn = conn;
    }

    public static class CombatStats {
        int level;
        int exp;
        int wisdom;
        int health;
        int strength;
        int critchance;
        int critdamage;
        int ability;

        public CombatStats(int level, int exp, int wisdom, int health, int strength, int critchance, int critdamage, int ability) {
            this.level = level;
            this.exp = exp;
            this.wisdom = wisdom;
            this.health = health;
            this.strength = strength;
            this.critchance = critchance;
            this.critdamage = critdamage;
            this.ability = ability;
        }

        public int getLevel() { return level; }
        public int getExp() { return exp; }
        public int getWisdom() { return wisdom; }
        public int getHealth() { return health; }
        public int getStrength() { return strength; }
        public int getCritchance() { return critchance; }
        public int getCritdamage() { return critdamage; }
        public int getAbility() { return ability; }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM combats WHERE member_uuid = ?";
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
        String sql = "INSERT INTO combats (member_uuid, level, exp, wisdom, health, strength, critchance, critdamage, ability) " +
                "VALUES (?, 1, 0, 0, 0, 0, 0, 0, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CombatStats getCombatStats(Player player) {
        String sql = "SELECT level, exp, wisdom, health, strength, critchance, critdamage, ability FROM combats WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CombatStats(
                            rs.getInt("level"),
                            rs.getInt("exp"),
                            rs.getInt("wisdom"),
                            rs.getInt("health"),
                            rs.getInt("strength"),
                            rs.getInt("critchance"),
                            rs.getInt("critdamage"),
                            rs.getInt("ability")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 기본값 반환 (DB 삽입은 init()에서만 함)
        return new CombatStats(1, 0, 0, 0, 0, 0, 0, 0);
    }

    public void setStat(Player player, String tag, int amount) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "health", "strength", "critchance", "critdamage", "ability");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE combats SET " + tag + " = ? WHERE member_uuid = ?";
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
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "health", "strength", "critchance", "critdamage", "ability");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "SELECT " + tag + " FROM combats WHERE member_uuid = ?";
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