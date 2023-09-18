package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtils.class);

    @Value("${jwt.secret}")
    private String secret;

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Set<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        LOG.info("claims = {}", claims.toString());
        LOG.info("token = {}", token);
        LOG.info("email = {}", getEmailFromToken(token));

        List<String> rolesTest = (List<String>) claims.get("ROLES");
        LOG.info("rolesTest = {}", rolesTest);

        /*Set<GrantedAuthority> roles = rolesTest.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());*/

        Set<GrantedAuthority> roles = rolesTest.stream()
                .filter(role -> role instanceof String)
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toSet());


        /*if (claims.containsKey("ROLES")) {
            List<String> roleStrings = (List<String>) claims.get("ROLES");

            // Convert role strings to GrantedAuthority objects
            roles = roleStrings.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }*/

        LOG.info("roles = {}", roles);
        return roles;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        LOG.info("secret size = {}", secret.length());
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

}
