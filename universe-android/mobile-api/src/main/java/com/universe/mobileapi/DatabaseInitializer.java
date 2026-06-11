package com.universe.mobileapi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    static void initialize() {
        if (!Config.autoMigrate()) {
            return;
        }
        String script = readScript();
        try (Connection connection = Database.open()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                for (String sql : script.split(";")) {
                    String command = sql.trim();
                    if (!command.isEmpty()) {
                        statement.execute(command);
                    }
                }
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "Không thể khởi tạo cơ sở dữ liệu: " + ex.getMessage(), ex);
        }
    }

    private static String readScript() {
        try (InputStream input = DatabaseInitializer.class.getClassLoader()
                .getResourceAsStream("db/cloud-schema.sql")) {
            if (input == null) {
                throw new IllegalStateException("Thiếu db/cloud-schema.sql.");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Không đọc được schema cloud.", ex);
        }
    }
}
