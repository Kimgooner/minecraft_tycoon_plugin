package org.kimgooner.tycoon.db.dao;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MemberDAO {
    private final Connection conn;

    public MemberDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertOrUpdateMember(UUID uuid) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO members (uuid, money) VALUES (?, 0)")) {
            ps.setString(1, uuid.toString());
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

    public void updateMoney(UUID uuid, Integer amount) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE members SET money = ? WHERE uuid = ?")) {
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

