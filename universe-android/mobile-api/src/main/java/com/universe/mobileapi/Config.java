package com.universe.mobileapi;

import java.net.URI;

final class Config {

    private Config() {
    }

    static String databaseUrl() {
        String configured = configuredDatabaseUrl();
        if (configured == null) {
            return "jdbc:postgresql://localhost:5433/universe";
        }
        return toJdbcUrl(configured);
    }

    static String toJdbcUrl(String configured) {
        if (configured.startsWith("jdbc:postgresql://")) {
            return configured;
        }
        if (!configured.startsWith("postgresql://")
                && !configured.startsWith("postgres://")) {
            throw new IllegalArgumentException(
                    "Database URL phải bắt đầu bằng jdbc:postgresql://, "
                            + "postgresql:// hoặc postgres://.");
        }

        URI uri = URI.create(configured);
        if (uri.getHost() == null || uri.getRawPath() == null
                || uri.getRawPath().isBlank()) {
            throw new IllegalArgumentException("Database URL không hợp lệ.");
        }
        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(uri.getHost())
                .append(':')
                .append(uri.getPort() > 0 ? uri.getPort() : 5432)
                .append(uri.getRawPath());
        if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }
        return jdbcUrl.toString();
    }

    static String databaseUser() {
        String configured = optionalEnv("UNIVERSE_DB_USER");
        if (configured != null) {
            return configured;
        }
        String[] credentials = credentialsFromUrl(configuredDatabaseUrl());
        return credentials == null ? "universe" : credentials[0];
    }

    static String databasePassword() {
        String configured = optionalEnv("UNIVERSE_DB_PASSWORD");
        if (configured != null) {
            return configured;
        }
        String[] credentials = credentialsFromUrl(configuredDatabaseUrl());
        return credentials == null ? "universe" : credentials[1];
    }

    static int port() {
        String platformPort = optionalEnv("PORT");
        return Integer.parseInt(platformPort != null
                ? platformPort
                : env("UNIVERSE_API_PORT", "8080"));
    }

    static long sessionTtlSeconds() {
        return Long.parseLong(env("UNIVERSE_SESSION_TTL_SECONDS", "43200"));
    }

    static boolean autoMigrate() {
        return Boolean.parseBoolean(env("UNIVERSE_AUTO_MIGRATE", "false"));
    }

    static String qrSigningSecret() {
        return env("UNIVERSE_QR_SECRET", "universe-qr-dev-secret-change-in-production");
    }

    static String[] credentialsFromUrl(String databaseUrl) {
        if (databaseUrl == null || databaseUrl.startsWith("jdbc:")) {
            return null;
        }
        URI uri = URI.create(databaseUrl);
        String userInfo = uri.getUserInfo();
        if (userInfo == null) {
            return null;
        }
        String[] parts = userInfo.split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        return parts;
    }

    private static String configuredDatabaseUrl() {
        String configured = optionalEnv("UNIVERSE_DB_URL");
        return configured == null ? optionalEnv("DATABASE_URL") : configured;
    }

    private static String optionalEnv(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String env(String name, String fallback) {
        String value = optionalEnv(name);
        return value == null ? fallback : value;
    }
}
