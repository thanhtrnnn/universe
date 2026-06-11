package com.universe.mobileapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigTest {

    @Test
    void convertsCloudPostgresUrlToJdbc() {
        String url = "postgresql://user:password@db.example.com:6543/universe"
                + "?sslmode=require";

        assertEquals(
                "jdbc:postgresql://db.example.com:6543/universe?sslmode=require",
                Config.toJdbcUrl(url));
    }

    @Test
    void usesDefaultPostgresPort() {
        assertEquals(
                "jdbc:postgresql://db.example.com:5432/universe",
                Config.toJdbcUrl(
                        "postgres://user:password@db.example.com/universe"));
    }

    @Test
    void decodesCloudCredentials() {
        assertArrayEquals(
                new String[]{"cloud+user", "p@ss:word"},
                Config.credentialsFromUrl(
                        "postgresql://cloud%2Buser:p%40ss%3Aword"
                                + "@db.example.com/universe"));
    }

    @Test
    void rejectsUnsupportedDatabaseUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Config.toJdbcUrl("mysql://db.example.com/universe"));
    }
}
