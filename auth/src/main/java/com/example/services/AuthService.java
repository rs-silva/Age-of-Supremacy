package com.example.services;

import com.example.dto.LoginResponseDTO;
import com.example.dto.RefreshTokenDTO;
import com.example.exceptions.InvalidCredentialsException;
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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(databaseUser.getId());
        String accessToken = jwtTokenUtils.generateToken(user);
        return new LoginResponseDTO(databaseUser.getId(), databaseUser.getEmail(), refreshToken.getToken(), accessToken);
    }

    public LoginResponseDTO loginUser(User loginUser) {
        User databaseUser = userService.findByEmail(loginUser.getEmail());
        boolean areCredentialsValid = passwordUtils.validateLoginPassword(loginUser.getPassword(), databaseUser.getPassword());

        if (!areCredentialsValid) {
            LOG.error("Wrong credentials for user {}", loginUser);
            throw new InvalidCredentialsException(AuthConstants.WRONG_LOGIN_CREDENTIALS);
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(databaseUser.getId());
        String accessToken = jwtTokenUtils.generateToken(databaseUser);
        return new LoginResponseDTO(databaseUser.getId(), databaseUser.getEmail(), refreshToken.getToken(), accessToken);
    }

    public RefreshTokenDTO refreshToken(String userEmail, String token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(token);

    }

}
