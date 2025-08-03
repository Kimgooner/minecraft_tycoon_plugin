package org.kimgooner.tycoon.db.dao;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataStorageDAO {
    private final Connection conn;

    public DataStorageDAO(Connection conn) {
        this.conn = conn;
    }

    public static class DataStorage {
        int slot_index;
        int amount;
        public DataStorage(int slot_index, int amount) {
            this.slot_index = slot_index;
            this.amount = amount;
        }
        public int getSlotIndex() {
            return slot_index;
        }
        public int getAmount() {
            return amount;
        }
    }

    public List<DataStorage> getDataStorageByCategory(Player player, int categoryId) {
        List<DataStorage> dataList = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM data_storage WHERE member_uuid = ? AND category_id = ? ORDER BY slot_index ASC"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, categoryId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dataList.add(new DataStorage(
                        rs.getInt("slot_index"),
                        rs.getInt("amount")
                ));
            }

            // 만약 데이터가 비어있으면 (처음 접속 등)
            if (dataList.isEmpty()) {
                initializeDataStorage(player, categoryId);
                return getDataStorageByCategory(player, categoryId);  // 재귀 호출로 초기화 후 재조회
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public void initializeDataStorage(Player player, int categoryId) {
        try {
            conn.setAutoCommit(false);  // 트랜잭션 시작
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO data_storage (member_uuid, category_id, slot_index, amount) VALUES (?, ?, ?, ?)"
            );

            for (int slot = 0; slot < 21; slot++) {
                insert.setString(1, player.getUniqueId().toString());
                insert.setInt(2, categoryId);
                insert.setInt(3, slot);
                insert.setInt(4, 0);  // 수량 0
                insert.addBatch();
            }
            insert.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addAmount(Player player, int categoryId, int slotIndex, int amountToAdd) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE data_storage SET amount = amount + ? WHERE member_uuid = ? AND category_id = ? AND slot_index = ?"
            );
            ps.setInt(1, amountToAdd);
            ps.setString(2, player.getUniqueId().toString());
            ps.setInt(3, categoryId);
            ps.setInt(4, slotIndex);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean removeAmount(Player player, int categoryId, int slotIndex, int amountToRemove) {
        try {
            // 현재 amount 조회
            PreparedStatement select = conn.prepareStatement(
                    "SELECT amount FROM data_storage WHERE member_uuid = ? AND category_id = ? AND slot_index = ?"
            );
            select.setString(1, player.getUniqueId().toString());
            select.setInt(2, categoryId);
            select.setInt(3, slotIndex);
            ResultSet rs = select.executeQuery();

            if (rs.next()) {
                int current = rs.getInt("amount");
                if (current <= 0) return false; // 감소할 수 없음

                int newAmount = Math.max(0, current - amountToRemove);

                PreparedStatement update = conn.prepareStatement(
                        "UPDATE data_storage SET amount = ? WHERE member_uuid = ? AND category_id = ? AND slot_index = ?"
                );
                update.setInt(1, newAmount);
                update.setString(2, player.getUniqueId().toString());
                update.setInt(3, categoryId);
                update.setInt(4, slotIndex);
                update.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasData(Player player, int categoryId) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM data_storage WHERE member_uuid = ? AND category_id = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) >= 21;  // 모든 슬롯이 존재하면 true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
