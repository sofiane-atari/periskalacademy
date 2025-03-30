package periskal.academy.backend.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import periskal.academy.backend.model.AuthenticateResponse;


public final class JwtUtil {

    private JwtUtil() {} // Private constructor to prevent instantiation
    private static final String SECRET_KEY = "qX12L3J8RkMbX9n5z2k7nA3j8yPw9DAbF2LtR3m+5Fw="; // Replace with a properly secured key, Generate a random key using a secure random number generator later? Leave to periskal?
    private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS256; 
    private static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000; // 10 hours in milliseconds, change to less for more security later and add refresh tokens? 

    // Generate the SecretKey instance from the base64-encoded secret key
    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

   // Generate JWT Token
    public static String generateToken(AuthenticateResponse authResponse) {
        return Jwts.builder()
                .subject(authResponse.getUsername())
                .claim("role", authResponse.getRole())
                .claim("id", authResponse.getId())
                .claim("firstname", authResponse.getFirstname())
                .claim("lastname", authResponse.getLastname())
                .claim("shipname", authResponse.getShipname())
                .claim("products", authResponse.getProducts())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) 
                .signWith(getSigningKey(), ALGORITHM) 
                .compact();
    }

    
    public static String extractUsername(String token) { //probably also redundant, but check where it is used in the codebase.
        return extractClaims(token).getSubject();
    }

    // Extract claims from the token (e.g., expiration, subject, etc.)
    private static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) 
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    
    public static boolean isTokenExpired(String token) { //redundant
        return extractClaims(token).getExpiration().before(new Date());
    }

    //Validate token and check if username matches, must be a better way of validationn check later. 
    //TODO: Checked and extractclaims validate/checkExpiry so why do I call those other 2 methods, I'm dumb. Even dumber that I check the username against itself in the filter. Make an issue for this!
    public static boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    public static Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractClaims(token);
        String role = claims.get("role", String.class);
        
        if (role != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        }
        
        return Collections.emptyList();
    }

}
