package periskal.academy.backend;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
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
    private JwtUtil jwtUtil;
    
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
        filter = new JwtAuthenticationFilter(jwtUtil);
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setContext(new SecurityContextImpl());
        
        // Get access to protected methods via reflection
        doFilterInternalMethod = JwtAuthenticationFilter.class.getDeclaredMethod(
            "doFilterInternal", 
            HttpServletRequest.class, 
            HttpServletResponse.class, 
            FilterChain.class
        );
        doFilterInternalMethod.setAccessible(true);
        
        shouldNotFilterMethod = JwtAuthenticationFilter.class.getDeclaredMethod(
            "shouldNotFilter",
            HttpServletRequest.class
        );
        shouldNotFilterMethod.setAccessible(true);
        
        
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        
        doFilterInternalMethod.invoke(filter, request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @Test
    void testNonBearerToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");
        
        doFilterInternalMethod.invoke(filter, request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @Test
    void testInvalidToken() throws Exception {
        String token = "invalid.jwt.token";
        String username = "testuser";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(eq(token), eq(username))).thenReturn(false);
        
        doFilterInternalMethod.invoke(filter, request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @Test
    void testExpiredJwtException() throws Exception {
        String token = "expired.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(
            new ExpiredJwtException(null, null, "JWT token has expired")
        );
        
        doFilterInternalMethod.invoke(filter, request, response, filterChain);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        printWriter.flush();
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("\"error\":\"token_expired\""));
        assertTrue(responseBody.contains("\"message\":\"JWT token has expired\""));
        verify(filterChain, never()).doFilter(any(), any());
    }
    
    @Test
    void testMalformedJwtException() throws Exception {
        String token = "malformed.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(
            new MalformedJwtException("Invalid JWT token")
        );
        
        doFilterInternalMethod.invoke(filter, request, response, filterChain);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        printWriter.flush();
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("\"error\":\"invalid_token\""));
        assertTrue(responseBody.contains("\"message\":\"Invalid JWT token\""));
        verify(filterChain, never()).doFilter(any(), any());
    }
    
    @Test
    void testSignatureException() throws Exception {
        String token = "invalid_signature.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(
            new SignatureException("Invalid JWT signature")
        );
        
        doFilterInternalMethod.invoke(filter, request, response, filterChain);
        
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        printWriter.flush();
        
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("\"error\":\"invalid_signature\""));
        assertTrue(responseBody.contains("\"message\":\"Invalid JWT signature\""));
        verify(filterChain, never()).doFilter(any(), any());
    }
    
}
