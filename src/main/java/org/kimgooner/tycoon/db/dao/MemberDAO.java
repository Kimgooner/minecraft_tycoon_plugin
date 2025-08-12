package org.kimgooner.tycoon.db.dao;

import org.bukkit.entity.Player;
import org.kimgooner.tycoon.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO {
    private final DatabaseManager databaseManager;

    public MemberDAO(DatabaseManager databaseManager) {
            this.databaseManager = databaseManager;
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM members WHERE uuid = ?";
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
        if (hasData(player)) return; // 이미 있으면 무시
        String sql = "INSERT INTO members (uuid, money) VALUES (?, 0)";
        try (Connection conn = databaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long getMoney(Player player) {
        try (Connection conn = databaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                "SELECT money FROM members WHERE uuid = ?")) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public void setMoney(Player player, int amount) {
        try (Connection conn = databaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = ? WHERE uuid = ?")) {
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean plusMoney(Player player, int amount) {
        try (Connection conn = databaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = money + ? WHERE uuid = ?")) {
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean minusMoney(Player player, int amount) {
        Long newMoney = getMoney(player) - amount;
        if (newMoney < 0) return false;
        try (Connection conn = databaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = ? WHERE uuid = ?")) {
            ps.setDouble(1, newMoney);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

