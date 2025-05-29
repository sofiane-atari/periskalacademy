package periskal.academy.backend.util;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.io.Encoders;
import periskal.academy.backend.model.AuthenticateResponse;


class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;
    private AuthenticateResponse authResponse;
    private static final String TEST_SECRET_KEY_BASE64 = Encoders.BASE64.encode("tFS57PEU9paRZu8idrCEMZ+78AhbKthv81HGhgTZpF4=".getBytes(StandardCharsets.UTF_8));

    private static final long TOKEN_VALIDITY_MS = 10L * 60 * 60 * 1000;

    @BeforeEach
    void setUp() {

        jwtUtil = new JwtUtil(TEST_SECRET_KEY_BASE64, TOKEN_VALIDITY_MS);

        authResponse = new AuthenticateResponse();
        authResponse.setUsername("testUser");
        authResponse.setRole("USER");
        authResponse.setUsername("testUser");
        authResponse.setRole("USER");
        authResponse.setId("P1");
        authResponse.setFirstname("John");
        authResponse.setLastname("Doe");
        authResponse.setShipname("BlackPearl");

        token = jwtUtil.generateToken(authResponse);
    }

    @Test
    void givenValidAuthenticateResponse_whenGenerateToken_thenTokenIsNotEmpty() {
        assertThat(token).isNotEmpty();
    }

    @Test
    void givenValidToken_whenExtractUsername_thenReturnsCorrectUsername() {
        String extractedUsername = jwtUtil.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(authResponse.getUsername());
    }

    @Test
    void givenValidToken_whenValidateToken_thenTokenIsValid() {
        assertThat(jwtUtil.validateToken(token, authResponse.getUsername())).isTrue();
    }

    @Test
    void givenTokenWithRole_whenExtractAuthorities_thenReturnsGrantedAuthority() {
        
        Collection<? extends GrantedAuthority> authorities = jwtUtil.extractAuthorities(token);

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }
    

    @Test
    void givenTokenWithoutRole_whenExtractAuthorities_thenReturnsEmptyCollection() {
        // given
        AuthenticateResponse authResponseWithoutRole = new AuthenticateResponse();
        authResponseWithoutRole.setUsername("testUserNoRole");
        authResponseWithoutRole.setRole(null); // no role set

        // when
        String tokenWithoutRole = jwtUtil.generateToken(authResponseWithoutRole);

        // assert
        Collection<? extends GrantedAuthority> authorities = jwtUtil.extractAuthorities(tokenWithoutRole);
        assertThat(authorities).isEmpty();
    }
}
