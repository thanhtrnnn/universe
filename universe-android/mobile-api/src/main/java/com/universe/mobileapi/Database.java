package com.universe.mobileapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class Database {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private Database() {
    }

    static Connection open() throws SQLException {
        return DriverManager.getConnection(
                Config.databaseUrl(),
                Config.databaseUser(),
                Config.databasePassword());
    }
}

