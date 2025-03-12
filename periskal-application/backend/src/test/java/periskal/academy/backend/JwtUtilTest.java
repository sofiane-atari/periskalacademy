package periskal.academy.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import periskal.academy.backend.util.JwtUtil;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;
    private String username;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        username = "testUser";
        token = jwtUtil.generateToken(username);
    }

    @Test
    public void testGenerateToken() {
        assertFalse(token.isEmpty());
    }

    @Test
    public void testExtractUsername() {
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testIsTokenExpired() {
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    public void testValidateToken() {
        assertTrue(jwtUtil.validateToken(token, username));
    }


}
