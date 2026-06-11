package com.universe.mobileapi;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class TokenService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    String create(String userId, String role) {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        sessions.put(token, new Session(
                userId,
                role,
                Instant.now().plusSeconds(Config.sessionTtlSeconds())));
        return token;
    }

    Principal resolve(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }
        if (session.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return null;
        }
        return new Principal(session.userId(), session.role());
    }

    void revoke(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    record Principal(String userId, String role) {
    }

    private record Session(String userId, String role, Instant expiresAt) {
    }
}
