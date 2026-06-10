package com.universe.util;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * Quản lý phiên đăng nhập qua Redis.
 * Sau khi {@code UserDAO.checkLogin()} thành công, sinh token UUID và lưu vào
 * Redis với key {@code session:{userId}} kèm TTL; đăng xuất thì xóa key.
 *
 * Lưu ý: đây là phần BỔ SUNG ngoài thiết kế docx gốc (Swing 1 tiến trình vốn
 * giữ phiên trong RAM); được thêm theo yêu cầu tích hợp Redis.
 */
public final class RedisUtil {

    private static final JedisPool POOL =
            new JedisPool(AppConfig.get("redis.host", "localhost"),
                          AppConfig.getInt("redis.port", 6379));

    private static final int TTL = AppConfig.getInt("redis.session.ttl", 900);

    private RedisUtil() {
    }

    /** Tạo token mới, lưu phiên cho userId và trả token. */
    public static String saveSession(String userId) {
        String token = UUID.randomUUID().toString();
        try (Jedis jedis = POOL.getResource()) {
            jedis.setex("session:" + userId, TTL, token);
        }
        return token;
    }

    public static String getSession(String userId) {
        try (Jedis jedis = POOL.getResource()) {
            return jedis.get("session:" + userId);
        }
    }

    public static void deleteSession(String userId) {
        try (Jedis jedis = POOL.getResource()) {
            jedis.del("session:" + userId);
        }
    }

    /** Kiểm tra Redis có sống không (dùng để báo lỗi thân thiện khi khởi động). */
    public static boolean ping() {
        try (Jedis jedis = POOL.getResource()) {
            return "PONG".equalsIgnoreCase(jedis.ping());
        } catch (Exception e) {
            return false;
        }
    }
}
