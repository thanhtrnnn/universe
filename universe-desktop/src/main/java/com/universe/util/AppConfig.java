package com.universe.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Đọc cấu hình từ resources/app.properties (DB, Redis, Kafka).
 * Dùng chung cho toàn bộ tầng hạ tầng.
 */
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy app.properties trong classpath");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private AppConfig() {
    }

    public static String get(String key) {
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String v = PROPS.getProperty(key);
        return v == null ? defaultValue : Integer.parseInt(v.trim());
    }
}
