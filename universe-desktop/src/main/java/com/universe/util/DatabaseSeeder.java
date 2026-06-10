package com.universe.util;

import com.universe.db.DBConnection;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Khởi tạo schema khi cần và chèn bù dữ liệu demo cho các bảng còn thiếu.
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() {
    }

    public static void main(String[] args) {
        seedIfNeeded();
    }

    public static void seedIfNeeded() {
        try (Connection con = DBConnection.getConnection()) {
            if (!hasUserTable(con)) {
                System.out.println("Bắt đầu khởi tạo CSDL từ schema.sql...");
                executeSqlScript(con, Files.readString(Path.of("schema.sql"), StandardCharsets.UTF_8));
            }

            executeSqlScript(con, readResource("/db/demo-seed.sql"));
            normalizeDemoUsernames(con);
            System.out.println("Dữ liệu demo UniVerse đã sẵn sàng.");
        } catch (Exception e) {
            System.err.println("Lỗi khi seed data: " + e.getMessage());
        }
    }

    private static boolean hasUserTable(Connection con) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeQuery("SELECT 1 FROM tblUser LIMIT 1").close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String readResource(String path) throws Exception {
        try (InputStream input = DatabaseSeeder.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Không tìm thấy resource " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void executeSqlScript(Connection con, String script) throws Exception {
        StringBuilder statement = new StringBuilder();
        boolean inString = false;

        for (String line : script.replace("\r", "").split("\n")) {
            String trimmed = line.trim();
            if (!inString && (trimmed.isEmpty() || trimmed.startsWith("--"))) {
                continue;
            }

            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch == '\'' && (i == 0 || line.charAt(i - 1) != '\\')) {
                    inString = !inString;
                }
                if (ch == ';' && !inString) {
                    executeStatement(con, statement.toString());
                    statement.setLength(0);
                } else {
                    statement.append(ch);
                }
            }
            statement.append('\n');
        }

        if (!statement.toString().isBlank()) {
            executeStatement(con, statement.toString());
        }
    }

    private static void executeStatement(Connection con, String sql) throws Exception {
        if (sql.isBlank()) {
            return;
        }
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void normalizeDemoUsernames(Connection con) throws Exception {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(
                    "UPDATE tblUser SET username = 'lecturer' " +
                    "WHERE id = 'GV01' AND username = 'dothilien'");
            stmt.executeUpdate(
                    "UPDATE tblUser SET username = 'student' " +
                    "WHERE id = 'S01' AND username = 'b23dcat120'");
            stmt.executeUpdate(
                    "UPDATE tblUser SET username = 'student2' " +
                    "WHERE id = 'S02' AND username = 'b23dcat280'");
            stmt.executeUpdate(
                    "UPDATE tblUser SET username = 'student3' " +
                    "WHERE id = 'S03' AND username = 'b23dccn266'");
        }
    }
}
