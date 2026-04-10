package com.journal.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Берём секретный ключ из application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Генерируем ключ из строки
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Создать токен — вызывается при логине
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)            // в токен записываем email
                .claim("role", role)          // и роль пользователя
                .setIssuedAt(new Date())      // когда создан
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // когда истекает
                .signWith(getKey())           // подписываем ключом
                .compact();
    }

    // Достать email из токена
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Достать роль из токена
    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    // Проверить что токен не истёк и подпись верная
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;  // токен неверный или истёк
        }
    }
}