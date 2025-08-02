package org.kimgooner.tycoon.db.dao;

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
        int stregnth;
        int critchance;
        int critdamage;
        int ability;

        public CombatStats(int level, int exp, int wisdom, int health, int stregnth, int critchance, int critdamage, int ability) {
            this.level = level;
            this.exp = exp;
            this.wisdom = wisdom;
            this.health = health;
            this.stregnth = stregnth;
            this.critchance = critchance;
            this.critdamage = critdamage;
            this.ability = ability;
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

        public int getHealth() {
            return health;
        }

        public int getStregnth() {
            return stregnth;
        }

        public int getCritchance() {
            return critchance;
        }

        public int getCritdamage() {
            return critdamage;
        }

        public int getAbility() {
            return ability;
        }
    }

    public CombatStats getCombatStats(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM combats WHERE member_uuid = ?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CombatStats(
                        rs.getInt("level"),
                        rs.getInt("exp"),
                        rs.getInt("wisdom"),
                        rs.getInt("health"),
                        rs.getInt("strength"),
                        rs.getInt("critchance"),
                        rs.getInt("critdamage"),
                        rs.getInt("ability"));
            } else {
                PreparedStatement insert = conn.prepareStatement("INSERT INTO combats (member_uuid, level, exp, wisdom, health, strength, critchance, critdamage, ability) VALUES (?, 1, 0, 0, 0, 0, 0, 0, 0)");
                insert.setString(1, player.getUniqueId().toString());
                insert.executeUpdate();
                return new CombatStats(1, 0, 0, 0, 0, 0, 0, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new CombatStats(1, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    public void setStat(Player player, String tag, int amount) {
        // 안전을 위해 컬럼명 검증 (SQL Injection 방지)
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "health", "stregnth", "critchance", "critdamage", "ability");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE combats SET " + tag + " = ? WHERE member_uuid = ?";
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
            PreparedStatement ps = conn.prepareStatement("SELECT ? FROM combats WHERE member_uuid = ?");
            ps.setString(1, tag);
            ps.setString(2, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(tag);
        } catch (SQLException e) {}
        return 0;
    }
}