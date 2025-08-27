package com.bullionx.authservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret-base64:}") String secretBase64) {
        if (secretBase64 == null || secretBase64.isBlank()) {
            // dev fallback: strong random key (tokens invalid after restart)
            this.key = Jwts.SIG.HS256.key().build();
        } else {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        }
    }

    public String generateToken(UUID id, String email, String firstName, String lastName) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(id.toString())
                .claim("email", email)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(10))))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
