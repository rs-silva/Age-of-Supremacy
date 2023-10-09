package com.example.services;

import com.example.dto.TokenResponseDTO;
import com.example.exceptions.UnauthorizedException;
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

    private final PasswordUtils passwordUtils;

    private final JwtTokenUtils jwtTokenUtils;

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserService userService, PasswordUtils passwordUtils, JwtTokenUtils jwtTokenUtils) {
        this.userService = userService;
        this.passwordUtils = passwordUtils;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public TokenResponseDTO registerUser(User user) {
        user.setPassword(passwordUtils.encodePassword(user.getPassword()));
        user.addRole(new SimpleGrantedAuthority("ROLE_USER"));
        User databaseUser = userService.addUserToDatabase(user);
        String accessToken = jwtTokenUtils.generateToken(user);
        return new TokenResponseDTO(databaseUser.getId(), databaseUser.getEmail(), accessToken);
    }

    public TokenResponseDTO loginUser(User loginUser) {
        User databaseUser = userService.findByEmail(loginUser.getEmail());
        boolean areCredentialsValid = passwordUtils.validateLoginPassword(loginUser.getPassword(), databaseUser.getPassword());

        if (!areCredentialsValid) {
            throw new UnauthorizedException(AuthConstants.WRONG_LOGIN_CREDENTIALS);
        }

        String accessToken = jwtTokenUtils.generateToken(databaseUser);
        return new TokenResponseDTO(databaseUser.getId(), databaseUser.getEmail(), accessToken);
    }

}
