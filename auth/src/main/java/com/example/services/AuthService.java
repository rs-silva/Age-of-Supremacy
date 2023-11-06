package com.example.services;

import com.example.dto.LoginRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.dto.RefreshTokenResponseDTO;
import com.example.exceptions.InvalidCredentialsException;
import com.example.exceptions.RefreshTokenException;
import com.example.models.RefreshToken;
import com.example.models.User;
import com.example.utils.AuthConstants;
import com.example.utils.JwtAccessTokenUtils;
import com.example.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

    private final PasswordUtils passwordUtils;

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserService userService, RefreshTokenService refreshTokenService, PasswordUtils passwordUtils, JwtAccessTokenUtils jwtAccessTokenUtils) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.passwordUtils = passwordUtils;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
    }

    public UserResponseDTO registerUser(User user) {
        user.setPassword(passwordUtils.encodePassword(user.getPassword()));
        user.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        User databaseUser = userService.addUserToDatabase(user);

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(databaseUser);
        String accessToken = jwtAccessTokenUtils.generateAccessToken(user);

        return new UserResponseDTO(databaseUser.getId(), databaseUser.getEmail(), databaseUser.getUsername(), refreshToken.getToken(), accessToken);
    }

    public UserResponseDTO loginUser(LoginRequestDTO loginUser) {
        User databaseUser = userService.findByEmail(loginUser.getEmail());
        boolean areCredentialsValid = passwordUtils.validateLoginPassword(loginUser.getPassword(), databaseUser.getPassword());

        if (!areCredentialsValid) {
            LOG.error("Wrong credentials for user {}", loginUser);
            throw new InvalidCredentialsException(AuthConstants.WRONG_LOGIN_CREDENTIALS);
        }

        refreshTokenService.deleteByUser(databaseUser);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(databaseUser);
        String accessToken = jwtAccessTokenUtils.generateAccessToken(databaseUser);

        return new UserResponseDTO(databaseUser.getId(), databaseUser.getEmail(), databaseUser.getUsername(), refreshToken.getToken(), accessToken);
    }

    public RefreshTokenResponseDTO refreshToken(String userEmail, String token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(token);
        User user = validateUserFromRequest(userEmail, refreshToken);
        refreshTokenService.verifyExpiration(refreshToken);

        String accessToken = jwtAccessTokenUtils.generateAccessToken(user);
        return new RefreshTokenResponseDTO(accessToken);
    }

    public void logout(String userEmail) {
        userService.validateTokenEmail(userEmail);

        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);

        User user = userService.findByEmail(userEmail);
        refreshTokenService.deleteByUser(user);
    }

    private User validateUserFromRequest(String userEmail, RefreshToken refreshToken) {
        User user = refreshToken.getUser();

        if (!userEmail.equals(user.getEmail())) {
            LOG.error("Refresh Token {} does not belong to the user with email {}", refreshToken.getToken(), user.getEmail());
            throw new RefreshTokenException(AuthConstants.REFRESH_TOKEN_DOES_NOT_BELONG_TO_USER);
        }

        return user;
    }

}
