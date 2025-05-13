package periskal.academy.backend.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import periskal.academy.backend.model.AuthenticateResponse;
import periskal.academy.backend.util.JwtUtil;

public class JwtUtilTest {

    JwtUtil jwtUtil;

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

    @Test
    void givenTokenWithRole_whenExtractAuthorities_thenReturnsGrantedAuthority() {
        // Given
        String token = "dummy.jwt.token";
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get("role", String.class)).thenReturn("admin");

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class, Answers.CALLS_REAL_METHODS)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(token)).thenReturn(mockClaims);

            // When
            Collection<? extends GrantedAuthority> authorities = JwtUtil.extractAuthorities(token);

            // Then
            assertThat(authorities).hasSize(1);
            assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Test
    void givenTokenWithoutRole_whenExtractAuthorities_thenReturnsEmptyCollection() {
        // Given
        String token = "dummy.jwt.token";
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get("role", String.class)).thenReturn(null);

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class, Answers.CALLS_REAL_METHODS)) {
            mockedJwtUtil.when(() -> JwtUtil.extractClaims(token)).thenReturn(mockClaims);

            // When
            Collection<? extends GrantedAuthority> authorities = JwtUtil.extractAuthorities(token);

            // Then
            assertThat(authorities).isEmpty();
        }
    }
}
