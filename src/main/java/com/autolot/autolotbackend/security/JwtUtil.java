package com.autolot.autolotbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String dealershipId, String email, String role){
        return Jwts.builder()
                .subject(userId)
                .claim("dealershipId", dealershipId)
                .claim("email",email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateJwtToken(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }catch (SecurityException e){
            System.out.println("Invalid JWT signature " + e.getMessage());
        }catch (MalformedJwtException e){
            System.out.println("Invalid JWT token: " + e.getMessage());
        }catch (ExpiredJwtException e){
            System.out.println("JWT token is expired: " + e.getMessage());
        }catch (UnsupportedJwtException e){
            System.out.println("JWT token is unsupported: " + e.getMessage());
        }catch (IllegalArgumentException e){
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }
}
