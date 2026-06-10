package com.universe.dao;

import com.universe.db.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở (Chương 4.3.2: "kế thừa lớp DAO để xử lý cơ chế dùng chung
 * truy cập vào cơ sở dữ liệu"). Mọi DAO nghiệp vụ kế thừa lớp này để dùng
 * chung cách lấy Connection JDBC.
 */
public abstract class DAO {

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
