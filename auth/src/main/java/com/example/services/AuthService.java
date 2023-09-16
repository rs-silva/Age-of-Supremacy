package com.example.services;

import com.example.dto.UserLoginDTO;
import com.example.exceptions.UnauthorizedException;
import com.example.models.User;
import com.example.utils.AuthConstants;
import com.example.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;

    private final PasswordUtils passwordUtils;

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(UserService userService, PasswordUtils passwordUtils) {
        this.userService = userService;
        this.passwordUtils = passwordUtils;
    }

    public void registerUser(User user) {
        user.setPassword(passwordUtils.encodePassword(user.getPassword()));
        userService.addUserToDatabase(user);
    }

    public String loginUser(UserLoginDTO loginUser) {
        User user = userService.findByUsername(loginUser.getUsername());
        boolean areCredentialsValid = passwordUtils.validateLoginCredentials(loginUser.getPassword(), user.getPassword());

        if (!areCredentialsValid) {
            throw new UnauthorizedException(AuthConstants.WRONG_LOGIN_CREDENTIALS);
        }
        return user.getPassword();
    }

}
