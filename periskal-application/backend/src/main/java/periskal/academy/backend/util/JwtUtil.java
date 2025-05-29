package periskal.academy.backend.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.model.AuthenticateResponse;

@Component
@Slf4j
public class JwtUtil {

    private final String secretKey; 
    private final long jwtTokenValidity;
    private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS256;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,@Value("${jwt.token-validity-ms}") long jwtTokenValidity) { 
        this.secretKey = secretKey;
        this.jwtTokenValidity = jwtTokenValidity;
        log.info("JwtUtil initialized with token validity {} ms", jwtTokenValidity);
    }


    // Generate the SecretKey instance from the base64-encoded secret key
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT Token
    public String generateToken(AuthenticateResponse authResponse) {
        return Jwts.builder()
                .subject(authResponse.getUsername())
                .claim("role", authResponse.getRole())
                .claim("id", authResponse.getId())
                .claim("firstname", authResponse.getFirstname())
                .claim("lastname", authResponse.getLastname())
                .claim("shipname", authResponse.getShipname())
                .claim("products", authResponse.getProducts())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtTokenValidity))
                .signWith(getSigningKey(), ALGORITHM)
                .compact();
    }

    public String extractUsername(String token) { 
        return extractClaims(token).getSubject();
    }

    // Extract claims from the token (e.g., expiration, subject, etc.)
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) { // redundant sinsce extractClaims should already checks expiry, but keep it for safety
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    public Collection<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractClaims(token);
        String role = claims.get("role", String.class);

        if (role != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }

        return Collections.emptyList();
    }

}
