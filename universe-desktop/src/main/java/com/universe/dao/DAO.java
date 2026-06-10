package com.universe.dao;

import com.universe.db.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở
 */
public abstract class DAO {

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
