package com.example.services;

import com.example.exceptions.RefreshTokenException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.RefreshToken;
import com.example.models.User;
import com.example.repositories.RefreshTokenRepository;
import com.example.utils.AuthConstants;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshTokenExpirationMs}")
    private Long JWT_REFRESH_TOKEN_VALIDITY;

    private final RefreshTokenRepository refreshTokenRepository;

    private static final Logger LOG = LoggerFactory.getLogger(RefreshTokenService.class);

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);

        if (refreshToken == null) {
            LOG.error("Refresh token {} was not found in the database", token);
            throw new RefreshTokenException(AuthConstants.REFRESH_TOKEN_NOT_FOUND);
        }

        return refreshToken;
    }

    public RefreshToken generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(getExpirationDate());
        refreshToken.setToken(doGenerateAccessToken());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(AuthConstants.REFRESH_TOKEN_EXPIRED);
        }
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY);
    }

    private String doGenerateAccessToken() {
        return UUID.randomUUID().toString();
    }
}
