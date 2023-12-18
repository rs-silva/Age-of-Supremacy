package com.example.filters;

import com.example.exceptions.ExpiredJwtTokenException;
import com.example.utils.AuthConstants;
import com.example.utils.JwtAccessTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    public JwtRequestFilter(JwtAccessTokenUtils jwtTokenUtil) {
        this.jwtAccessTokenUtils = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String email = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = jwtAccessTokenUtils.retrieveTokenFromRequest();
            try {
                email = jwtAccessTokenUtils.retrieveEmailFromRequestToken();
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                throw new ExpiredJwtTokenException(AuthConstants.EXPIRED_JWT_TOKEN);
            }
        } else {
            //logger.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtAccessTokenUtils.validateToken(jwtToken)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        email, null, jwtAccessTokenUtils.getAuthoritiesFromToken(jwtToken));
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                LOG.info("User logged in = {}", email);
            }
        }
        chain.doFilter(request, response);
    }

}
