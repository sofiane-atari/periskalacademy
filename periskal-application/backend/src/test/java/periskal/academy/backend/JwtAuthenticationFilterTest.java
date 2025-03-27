package periskal.academy.backend;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import periskal.academy.backend.security.JwtAuthenticationFilter;
import periskal.academy.backend.util.JwtUtil;

class JwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private Method doFilterInternalMethod;
    private Method shouldNotFilterMethod;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter();
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(new SecurityContextImpl());

        // Get access to protected methods via reflection
        doFilterInternalMethod = JwtAuthenticationFilter.class.getDeclaredMethod(
                "doFilterInternal",
                HttpServletRequest.class,
                HttpServletResponse.class,
                FilterChain.class);
        doFilterInternalMethod.setAccessible(true);

        shouldNotFilterMethod = JwtAuthenticationFilter.class.getDeclaredMethod(
                "shouldNotFilter",
                HttpServletRequest.class);
        shouldNotFilterMethod.setAccessible(true);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void givenNoAuthorizationHeader_whenDoFilterInternal_thenAuthenticationIsNotSet() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        doFilterInternalMethod.invoke(filter, request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void givenNonBearerToken_whenDoFilterInternal_thenAuthenticationIsNotSet() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");

        doFilterInternalMethod.invoke(filter, request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void givenInvalidToken_whenDoFilterInternal_thenAuthenticationIsNotSet() throws Exception {
        String token = "invalid.jwt.token";
        String username = "testuser";

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            mockedJwtUtil.when(() -> JwtUtil.extractUsername(token)).thenReturn(username);
            mockedJwtUtil.when(() -> JwtUtil.validateToken(eq(token), eq(username))).thenReturn(false);

            doFilterInternalMethod.invoke(filter, request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    @Test
    void givenExpiredJwtToken_whenDoFilterInternal_thenReturnsUnauthorizedError() throws Exception {
        String token = "expired.jwt.token";

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            mockedJwtUtil.when(() -> JwtUtil.extractUsername(token)).thenThrow(
                    new ExpiredJwtException(null, null, "JWT token has expired"));

            doFilterInternalMethod.invoke(filter, request, response, filterChain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            printWriter.flush();

            String responseBody = stringWriter.toString();
            assertThat(responseBody).contains("\"error\":\"token_expired\"");
            assertThat(responseBody).contains("\"message\":\"JWT token has expired\"");
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    @Test
    void givenMalformedJwtToken_whenDoFilterInternal_thenReturnsUnauthorizedError() throws Exception {
        String token = "malformed.jwt.token";

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            mockedJwtUtil.when(() -> JwtUtil.extractUsername(token)).thenThrow(
                    new MalformedJwtException("Invalid JWT token"));

            doFilterInternalMethod.invoke(filter, request, response, filterChain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            printWriter.flush();

            String responseBody = stringWriter.toString();
            assertThat(responseBody).contains("\"error\":\"invalid_token\"");
            assertThat(responseBody).contains("\"message\":\"Invalid JWT token\"");
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    @Test
    void testSignatureException() throws Exception {
        String token = "invalid_signature.jwt.token";

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            mockedJwtUtil.when(() -> JwtUtil.extractUsername(token)).thenThrow(
                    new SignatureException("Invalid JWT signature"));

            doFilterInternalMethod.invoke(filter, request, response, filterChain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            printWriter.flush();

            String responseBody = stringWriter.toString();
            assertThat(responseBody).contains("\"error\":\"invalid_signature\"");
            assertThat(responseBody).contains("\"message\":\"Invalid JWT signature\"");
            verify(filterChain, never()).doFilter(any(), any());
        }
    }
}
