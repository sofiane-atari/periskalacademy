package periskal.academy.backend.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import periskal.academy.backend.model.AuthenticateResponse;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "qX12L3J8RkMbX9n5z2k7nA3j8yPw9DAbF2LtR3m+5Fw="; // Replace with a properly secured key, Generate a random key using a secure random number generator later? Leave to periskal?
    private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS256; 

    // Generate the SecretKey instance from the base64-encoded secret key
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

   // Generate JWT Token
    public String generateToken(AuthenticateResponse authResponse) {
        return Jwts.builder()
                .subject(authResponse.getUsername())
                .claim("role", authResponse.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(getSigningKey(), ALGORITHM) // Use the new JJWT signature API
                .compact();
    }

    // Extract the username (subject) from the token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract claims from the token (e.g., expiration, subject, etc.)
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) 
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Validate the token
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Validate the token and check if the username matches
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
