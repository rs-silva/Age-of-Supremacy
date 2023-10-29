package com.example.utils;

import com.example.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    private final HttpServletRequest request;

    @Value("{jwt.accessTokenExpirationMs}")
    private long JWT_ACCESS_TOKEN_VALIDITY;

    public JwtTokenUtils(HttpServletRequest request) {
        this.request = request;
    }

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
        return new Date(System.currentTimeMillis() + JWT_ACCESS_TOKEN_VALIDITY);
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String retrieveTokenFromRequest() {
        String auth = request.getHeader("Authorization");
        return auth.substring(7);
    }

    public String retrieveEmailFromRequestToken() {
        String token = retrieveTokenFromRequest();
        return getEmailFromToken(token);
    }

    private String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Set<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        List<LinkedHashMap<String, String>> roles = (List<LinkedHashMap<String, String>>) claims.get("ROLES");

        return convertRolesToAuthorities(roles);
    }

    private Set<GrantedAuthority> convertRolesToAuthorities(List<LinkedHashMap<String, String>> roles) {
        return roles.stream()
                .map(roleMap -> roleMap.get("authority"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

}
