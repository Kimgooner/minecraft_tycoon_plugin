package org.kimgooner.tycoon.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static Connection connection;

    public static void init() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite::memory:"); //테스트용으로 DB 메모리 사용.
            Statement stmt = connection.createStatement();

            // members
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS members (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT UNIQUE NOT NULL,
                    money BIGINT DEFAULT 0
                );
            """);

            // minings
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS minings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_uuid TEXT NOT NULL,
                    level INTEGER DEFAULT 1,
                    exp INTEGER DEFAULT 0,
                    wisdom INTEGER DEFAULT 0,
                    fortune INTEGER DEFAULT 0,
                    speed INTEGER DEFAULT 0,
                    pristine INTEGER DEFAULT 0,
                    power INTEGER DEFAULT 0,
                    light INTEGER DEFAULT 0,
                    FOREIGN KEY(member_uuid) REFERENCES members(uuid)
                );
            """);

            // farmings
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS farmings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_uuid TEXT NOT NULL,
                    level INTEGER DEFAULT 1,
                    exp INTEGER DEFAULT 0,
                    wisdom INTEGER DEFAULT 0,
                    fortune INTEGER DEFAULT 0,
                    richness INTEGER DEFAULT 0,
                    FOREIGN KEY(member_uuid) REFERENCES members(uuid)
                );
            """);

            // fishings
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fishings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_uuid TEXT NOT NULL,
                    level INTEGER DEFAULT 1,
                    exp INTEGER DEFAULT 0,
                    wisdom INTEGER DEFAULT 0,
                    speed INTEGER DEFAULT 0,
                    multihook INTEGER DEFAULT 0,
                    wonder INTEGER DEFAULT 0,
                    FOREIGN KEY(member_uuid) REFERENCES members(uuid)
                );
            """);

            // combats
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS combats (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    member_uuid TEXT NOT NULL,
                    level INTEGER DEFAULT 1,
                    exp INTEGER DEFAULT 0,
                    wisdom INTEGER DEFAULT 0,
                    health INTEGER DEFAULT 0,
                    strength INTEGER DEFAULT 0,
                    critchance INTEGER DEFAULT 0,
                    critdamage INTEGER DEFAULT 0,
                    ability INTEGER DEFAULT 0,
                    FOREIGN KEY(member_uuid) REFERENCES members(uuid)
                );
            """);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
