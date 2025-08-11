package org.kimgooner.tycoon.db.dao.mining;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HeartDAO {
    private final Connection conn;
    public HeartDAO(Connection conn) {
        this.conn = conn;
    }

    public Integer getLevel(Player player, Integer index) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT value FROM cave_heart WHERE member_uuid = ? AND slot_index = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, index);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {return  rs.getInt(1);}
        } catch (SQLException e) {}
        return 0;
    }

    public void setStat(Player player, Integer index, Integer value) {
        try {
            conn.setAutoCommit(false); // 트랜잭션 시작
            PreparedStatement ps = conn.prepareStatement("UPDATE cave_heart SET value = ? WHERE member_uuid = ? AND slot_index = ?");
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.setInt(3, index);
            ps.executeUpdate();
            conn.commit(); // 커밋
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLevel(Player player, Integer index) {
        try {
            conn.setAutoCommit(false); // 트랜잭션 시작
            PreparedStatement ps = conn.prepareStatement("UPDATE cave_heart SET value = value + 1 WHERE member_uuid = ? AND slot_index = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, index);
            ps.executeUpdate();
            conn.commit(); // 커밋
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init(Player player) {
        try {
            conn.setAutoCommit(false); // 트랜잭션 시작
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT IGNORE INTO cave_heart (member_uuid, slot_index, value) VALUES (?, ?, 0)"
            );
            for (int i = 1; i <= 33; i++) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setInt(2, i);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit(); // 커밋
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    public boolean hasData(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM cave_heart WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) >= 33;  // 모든 슬롯이 존재하면 true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}