package com.example.utils;

import com.example.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

    public String generateToken(User user) {
        Map<String, Set<GrantedAuthority>> roles = new HashMap<>();
        roles.put("ROLES", user.getAuthorities());

        return Jwts.builder()
                .setClaims(roles)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY);
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
