package com.universe.db;

import com.universe.util.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý kết nối JDBC tới PostgreSQL (Chương 2.4.1: "kết nối CSDL qua JDBC").
 * Mỗi lần gọi getConnection() mở một kết nối mới; tầng DAO chịu trách nhiệm đóng.
 */
public final class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        URL = AppConfig.get("db.url");
        USER = AppConfig.get("db.user");
        PASSWORD = AppConfig.get("db.password");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Thiếu PostgreSQL JDBC driver: " + e.getMessage());
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
