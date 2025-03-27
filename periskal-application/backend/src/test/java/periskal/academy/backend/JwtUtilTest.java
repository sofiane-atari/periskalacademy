package periskal.academy.backend;

import static org.assertj.core.api.Assertions.*;

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
    public void givenValidAuthenticateResponse_whenGenerateToken_thenTokenIsNotEmpty() {
        assertThat(token).isNotEmpty();
    }

    @Test
    public void givenValidToken_whenExtractUsername_thenReturnsCorrectUsername() {
        String extractedUsername = JwtUtil.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(authResponse.getUsername());
    }

    @Test
    public void givenValidToken_whenCheckIfExpired_thenTokenIsNotExpired() {
        assertThat(JwtUtil.isTokenExpired(token)).isFalse();
    }

    @Test
    public void givenValidToken_whenValidateToken_thenTokenIsValid() {
        assertThat(JwtUtil.validateToken(token, authResponse.getUsername())).isTrue();
    }
}
