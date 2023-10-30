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

    private final UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(RefreshTokenService.class);

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    public RefreshToken findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);

        if (refreshToken == null) {
            LOG.error("Refresh token {} was not found in the database", token);
            throw new ResourceNotFoundException(AuthConstants.REFRESH_TOKEN_NOT_FOUND);
        }

        return refreshToken;
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userService.findById(userId));
        refreshToken.setExpiryDate(getExpirationDate());
        refreshToken.setToken(generateToken());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().after(new Date())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(AuthConstants.REFRESH_TOKEN_EXPIRED);
        }
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userService.findById(userId);
        refreshTokenRepository.deleteByUser(user);
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}