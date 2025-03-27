package periskal.academy.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.util.JwtUtil;

public class JwtUtilTest {

    private String token;
    private AuthenticateResponse authResponse;

    @BeforeEach
    public void setUp() {
        authResponse = new AuthenticateResponse();
        authResponse.setUsername("testUser");
        authResponse.setRole("USER");
        token = JwtUtil.generateToken(authResponse);
    }

    @Test
    public void testGenerateToken() {
        assertFalse(token.isEmpty());
    }

    @Test
    public void testExtractUsername() {
        String extractedUsername = JwtUtil.extractUsername(token);
        assertEquals(authResponse.getUsername(), extractedUsername);
    }

    @Test
    public void testIsTokenExpired() {
        assertFalse(JwtUtil.isTokenExpired(token));
    }

    @Test
    public void testValidateToken() {
        assertTrue(JwtUtil.validateToken(token, authResponse.getUsername()));
    }

    
}
