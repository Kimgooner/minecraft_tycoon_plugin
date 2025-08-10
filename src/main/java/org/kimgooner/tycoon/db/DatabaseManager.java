package org.kimgooner.tycoon.db;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.util.EnvLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static Connection connection;

    public static void init(JavaPlugin plugin) {
        try {
            EnvLoader loader = new EnvLoader(plugin);
            String DB_URL = loader.getDBUrl();
            String USER_NAME = loader.getUSERNAME();
            String PASSWORD = loader.getUSERPASSWORD();
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL,
                    USER_NAME,
                    PASSWORD
                    ); //테스트용으로 DB 메모리 사용.
            dropAllTables();
            Statement stmt = connection.createStatement();

            // members
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS members (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    uuid varchar(100) UNIQUE NOT NULL,
                    money BIGINT DEFAULT 0,
                    UNIQUE(uuid)
                );
            """);

            // minings
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS minings (
                    id INTEGER PRIMARY KEY AUTO_INCREMENT,
                    member_uuid varchar(100) NOT NULL,
                    level INTEGER DEFAULT 1,
                    exp INTEGER DEFAULT 0,
                    wisdom INTEGER DEFAULT 0,
                    fortune INTEGER DEFAULT 0,
                    speed INTEGER DEFAULT 0,
                    pristine INTEGER DEFAULT 0,
                    power INTEGER DEFAULT 0,
                    light INTEGER DEFAULT 0,
                    spread INTEGER DEFAULT 0,
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
                    exp INTEGER DEFAULT 0,
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
                    exp INTEGER DEFAULT 0,
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
                    exp INTEGER DEFAULT 0,
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

            // cave_heart
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

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void dropAllTables() {
        try {
            Statement stmt = connection.createStatement();

            // 순서는 FK 제약 때문에 의존성 고려해 삭제 (자식 테이블부터)
            stmt.executeUpdate("DROP TABLE IF EXISTS cave_heart;");
            stmt.executeUpdate("DROP TABLE IF EXISTS data_storage;");
            stmt.executeUpdate("DROP TABLE IF EXISTS combats;");
            stmt.executeUpdate("DROP TABLE IF EXISTS fishings;");
            stmt.executeUpdate("DROP TABLE IF EXISTS farmings;");
            stmt.executeUpdate("DROP TABLE IF EXISTS minings;");
            stmt.executeUpdate("DROP TABLE IF EXISTS members;");

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
