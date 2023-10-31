package com.example.services;

import com.example.dto.LoginResponseDTO;
import com.example.dto.RefreshTokenResponseDTO;
import com.example.exceptions.InvalidCredentialsException;
import com.example.exceptions.RefreshTokenException;
import com.example.models.RefreshToken;
import com.example.models.User;
import com.example.utils.AuthConstants;
import com.example.utils.JwtTokenUtils;
import com.example.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final PasswordUtils passwordUtils;

    private final JwtTokenUtils jwtTokenUtils;

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserService userService, RefreshTokenService refreshTokenService, PasswordUtils passwordUtils, JwtTokenUtils jwtTokenUtils) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.passwordUtils = passwordUtils;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public LoginResponseDTO registerUser(User user) {
        user.setPassword(passwordUtils.encodePassword(user.getPassword()));
        user.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        User databaseUser = userService.addUserToDatabase(user);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(databaseUser);
        String accessToken = jwtTokenUtils.generateAccessToken(user);
        return new LoginResponseDTO(databaseUser.getId(), databaseUser.getEmail(), refreshToken.getToken(), accessToken);
    }

    public LoginResponseDTO loginUser(User loginUser) {
        User databaseUser = userService.findByEmail(loginUser.getEmail());
        boolean areCredentialsValid = passwordUtils.validateLoginPassword(loginUser.getPassword(), databaseUser.getPassword());

        if (!areCredentialsValid) {
            LOG.error("Wrong credentials for user {}", loginUser);
            throw new InvalidCredentialsException(AuthConstants.WRONG_LOGIN_CREDENTIALS);
        }

        refreshTokenService.deleteByUser(databaseUser);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(databaseUser);
        String accessToken = jwtTokenUtils.generateAccessToken(databaseUser);
        return new LoginResponseDTO(databaseUser.getId(), databaseUser.getEmail(), refreshToken.getToken(), accessToken);
    }

    public RefreshTokenResponseDTO refreshToken(String userEmail, String token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(token);
        User user = validateUserFromRequest(userEmail, refreshToken);
        refreshTokenService.verifyExpiration(refreshToken);
        String accessToken = jwtTokenUtils.generateAccessToken(user);
        return new RefreshTokenResponseDTO(accessToken);
    }

    private User validateUserFromRequest(String userEmail, RefreshToken refreshToken) {
        User user = refreshToken.getUser();

        if (!userEmail.equals(user.getEmail())) {
            LOG.error("Token {} does not belong to the user with email {}", refreshToken.getToken(), user.getEmail());
            throw new RefreshTokenException(AuthConstants.REFRESH_TOKEN_DOES_NOT_BELONG_TO_USER);
        }

        return user;
    }

}
