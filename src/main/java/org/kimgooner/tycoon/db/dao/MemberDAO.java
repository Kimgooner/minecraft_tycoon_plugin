package org.kimgooner.tycoon.db.dao;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO {
    private final Connection conn;

    public MemberDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM members WHERE uuid = ?";
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
        String sql = "INSERT INTO members (uuid, money) VALUES (?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long getMoney(Player player) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT money FROM members WHERE uuid = ?")) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong("money");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public void setMoney(Player player, Integer amount) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = ? WHERE uuid = ?")) {
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean plusMoney(Player player, Integer amount) {
        Long newMoney = getMoney(player) + amount;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = ? WHERE uuid = ?")) {
            conn.setAutoCommit(false);
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean minusMoney(Player player, Integer amount) {
        Long newMoney = getMoney(player) - amount;
        if (newMoney < 0) return false;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = ? WHERE uuid = ?")) {
            conn.setAutoCommit(false);
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

