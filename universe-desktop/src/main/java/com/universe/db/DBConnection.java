package com.universe.db;

import com.universe.util.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Quản lý kết nối JDBC tới PostgreSQL (Chương 2.4.1: "kết nối CSDL qua JDBC").
 *
 * Dùng HikariCP làm connection pool: thay vì mở kết nối mới mỗi lần (mỗi lần là
 * một lần bắt tay TCP+TLS tới Render ~ vài trăm ms, gây đơ UI khi đổi tab), pool
 * giữ sẵn các kết nối "nóng" và tái sử dụng nên truy vấn nhanh hơn rất nhiều.
 * API {@link #getConnection()} giữ nguyên: DAO vẫn dùng try-with-resources, khi
 * close() kết nối được trả về pool chứ không đóng hẳn.
 */
public final class DBConnection {

    private static final HikariDataSource DS;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Thiếu PostgreSQL JDBC driver: " + e.getMessage());
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(AppConfig.get("db.url"));
        cfg.setUsername(AppConfig.get("db.user"));
        cfg.setPassword(AppConfig.get("db.password"));
        cfg.setPoolName("universe-pool");
        // Render free Postgres giới hạn số kết nối -> giữ pool nhỏ.
        cfg.setMaximumPoolSize(AppConfig.getInt("db.pool.maxSize", 5));
        cfg.setMinimumIdle(AppConfig.getInt("db.pool.minIdle", 1));
        cfg.setConnectionTimeout(10000);
        // Render đóng kết nối idle: giữ sống + tái tạo định kỳ để tránh "stale".
        cfg.setKeepaliveTime(30000);
        cfg.setMaxLifetime(120000);
        DS = new HikariDataSource(cfg);
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DS.getConnection();
    }
}
