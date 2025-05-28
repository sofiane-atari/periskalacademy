package periskal.academy.backend.util;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Encoders;
import periskal.academy.backend.model.AuthenticateResponse;


public class JwtUtilTest {

    private String token;
    private AuthenticateResponse authResponse;
    private static final String TEST_SECRET_KEY_BASE64 = Encoders.BASE64.encode("tFS57PEU9paRZu8idrCEMZ+78AhbKthv81HGhgTZpF4=".getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    public void setUp() {

        JwtUtil.setSecretKeyForTesting(TEST_SECRET_KEY_BASE64);

        authResponse = new AuthenticateResponse();
        authResponse.setUsername("testUser");
        authResponse.setRole("USER");
        token = JwtUtil.generateToken(authResponse);
    }

    @AfterEach
    public void tearDown() {
        // Clean up the static field just in case
        JwtUtil.setSecretKeyForTesting(null);
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
