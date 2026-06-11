package com.universe.mobileapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenServiceTest {

    @Test
    void retainsAuthenticatedRole() {
        TokenService service = new TokenService();
        String token = service.create("GV01", "Lecturer");

        TokenService.Principal principal = service.resolve(token);

        assertEquals("GV01", principal.userId());
        assertEquals("Lecturer", principal.role());
    }
}
