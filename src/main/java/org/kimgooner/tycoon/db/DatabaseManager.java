package org.kimgooner.tycoon.db;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init(JavaPlugin plugin) {
        try (Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement()) {

            //dropAllTables(connection); // 테스트용, 나중엔 필히 삭제.
            clearAllTables(connection);
            // 플레이어 정보
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS members (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                uuid varchar(100) UNIQUE NOT NULL,
                money BIGINT DEFAULT 0,
                UNIQUE(uuid)
            );
        """);

            // 채광 레벨
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS minings (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                level INTEGER DEFAULT 1,
                exp DOUBLE DEFAULT 0,
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid)
            );
        """);
            // 동굴의 심장 스텟 레벨 정보
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS cave_heart (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                slot_index INTEGER NOT NULL,
                value INTEGER NOT NULL,
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid, slot_index)
            );
        """);
            // 동굴의 심장 정보
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS cave_heart_info (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                level INTEGER NOT NULL,
                heart_key INTEGER NOT NULL,
                low_powder INTEGER NOT NULL,
                high_powder INTEGER NOT NULL,
                used_heart_key INTEGER NOT NULL,
                used_low_powder INTEGER NOT NULL,
                used_high_powder INTEGER NOT NULL,
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid)
            );
        """);

            // farmings
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS farmings (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                level INTEGER DEFAULT 1,
                exp DOUBLE DEFAULT 0,
                wisdom INTEGER DEFAULT 0,
                fortune INTEGER DEFAULT 0,
                richness INTEGER DEFAULT 0,
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid)
            );
        """);

            // fishings
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS fishings (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                level INTEGER DEFAULT 1,
                exp DOUBLE DEFAULT 0,
                wisdom INTEGER DEFAULT 0,
                speed INTEGER DEFAULT 0,
                multihook INTEGER DEFAULT 0,
                wonder INTEGER DEFAULT 0,
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid)
            );
        """);

            // combats
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS combats (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                level INTEGER DEFAULT 1,
                exp DOUBLE DEFAULT 0,
                wisdom INTEGER DEFAULT 0,
                health INTEGER DEFAULT 0,
                strength INTEGER DEFAULT 0,
                critchance INTEGER DEFAULT 0,
                critdamage INTEGER DEFAULT 0,
                ability INTEGER DEFAULT 0,
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid)
            );
        """);

            // data_storage
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS data_storage (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                member_uuid varchar(100) NOT NULL,
                category_id INTEGER NOT NULL,  -- 1: 채광, 2: 농사, 3: 낚시, 4: 전투 등
                slot_index INTEGER NOT NULL,   -- 0 ~ 20, 슬롯 번호 (21칸)
                amount INTEGER DEFAULT 0,       -- 보유 수량
                FOREIGN KEY(member_uuid) REFERENCES members(uuid),
                UNIQUE(member_uuid, category_id, slot_index)
            );
        """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropAllTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS cave_heart_info;");
            stmt.executeUpdate("DROP TABLE IF EXISTS cave_heart;");
            stmt.executeUpdate("DROP TABLE IF EXISTS data_storage;");
            stmt.executeUpdate("DROP TABLE IF EXISTS combats;");
            stmt.executeUpdate("DROP TABLE IF EXISTS fishings;");
            stmt.executeUpdate("DROP TABLE IF EXISTS farmings;");
            stmt.executeUpdate("DROP TABLE IF EXISTS minings;");
            stmt.executeUpdate("DROP TABLE IF EXISTS members;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAllTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM cave_heart_info;");
            stmt.executeUpdate("DELETE FROM cave_heart;");
            stmt.executeUpdate("DELETE FROM data_storage;");
            stmt.executeUpdate("DELETE FROM combats;");
            stmt.executeUpdate("DELETE FROM fishings;");
            stmt.executeUpdate("DELETE FROM farmings;");
            stmt.executeUpdate("DELETE FROM minings;");
            stmt.executeUpdate("DELETE FROM members;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setColumnsOnlinePlayers(Connection connection) throws SQLException {}

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not initialized.");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
