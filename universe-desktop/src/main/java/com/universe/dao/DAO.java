package com.universe.dao;

import com.universe.db.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở.
 * Giữ thuộc tính {@code con} đúng theo sơ đồ lớp; thực tế mỗi thao tác
 * mượn 1 connection từ pool HikariCP rồi trả lại qua try-with-resources.
 */
public abstract class DAO {

    protected Connection con;

    public DAO() {
    }

    /** Mượn 1 connection từ pool HikariCP. */
    protected Connection getConnection() throws SQLException {
        this.con = DBConnection.getConnection();
        return this.con;
    }
}
