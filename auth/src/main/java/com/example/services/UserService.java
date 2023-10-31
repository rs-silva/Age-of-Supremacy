package com.example.services;

import com.example.dto.LoginResponseDTO;
import com.example.exceptions.ForbiddenException;
import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.RefreshToken;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.AuthConstants;
import com.example.utils.JwtAccessTokenUtils;
import com.example.utils.PasswordUtils;
import com.example.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    private final PasswordUtils passwordUtils;

    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, JwtAccessTokenUtils jwtAccessTokenUtils, PasswordUtils passwordUtils, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
        this.passwordUtils = passwordUtils;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponseDTO updateUser(String userId, String currentUserEmail, User updatedUser) {
        User userFromId = findById(Long.valueOf(userId));

        validateUserEmailFromRequestParam(userFromId, currentUserEmail);
        validateTokenEmail(currentUserEmail);

        updatedUser.setPassword(passwordUtils.encodePassword(updatedUser.getPassword()));
        User newUser = UserUtils.updateUser(userFromId, updatedUser);

        refreshTokenService.deleteByUser(newUser);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(newUser);
        String accessToken = jwtAccessTokenUtils.generateAccessToken(newUser);
        userRepository.save(newUser);
        return new LoginResponseDTO(newUser.getId(), newUser.getEmail(), refreshToken.getToken(), accessToken);
    }

    public void deleteUser(String userId, String currentUserEmail) {
        User userFromId = findById(Long.valueOf(userId));

        validateUserEmailFromRequestParam(userFromId, currentUserEmail);
        validateTokenEmail(currentUserEmail);

        refreshTokenService.deleteByUser(userFromId);
        userRepository.deleteById(Long.valueOf(userId));
    }

    private void validateUserEmailFromRequestParam(User userFromId, String email) {
        String emailFromId = userFromId.getEmail();

        if (!emailFromId.equals(email)) {
            LOG.error("User email from given id does not match the email from the request parameter! Email from given id = " + emailFromId + " | Email from Request Parameter = " + email);
            throw new ForbiddenException(
                    String.format(AuthConstants.USER_EMAIL_FROM_GIVEN_ID_DOES_NOT_MATCH_EMAIL_FROM_REQUEST_PARAM));
        }
    }

    private void validateTokenEmail(String email) {
        String emailFromToken = jwtAccessTokenUtils.retrieveEmailFromRequestToken();

        if (!emailFromToken.equals(email)) {
            LOG.error("Email from Customer request = " + email + " | Email from token = " + emailFromToken);
            throw new ForbiddenException(
                    String.format(AuthConstants.CUSTOMER_EMAIL_DIFFERENT_FROM_TOKEN_EMAIL));
        }
    }

    public User addUserToDatabase(User newUser) {
        User user = userRepository.findByEmail(newUser.getEmail());

        if (user != null) {
            throw new ResourceAlreadyExistsException(
                    String.format(AuthConstants.USER_ALREADY_EXISTS_EXCEPTION, newUser.getEmail()));
        }

        LOG.info("Added user {} to database!", newUser.getEmail());
        return userRepository.save(newUser);
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format(AuthConstants.USER_WITH_ID_NOT_FOUND_EXCEPTION, id));
        }

        LOG.info("Found user {}", user);
        return user.get();
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException(
                    String.format(AuthConstants.USER_WITH_EMAIL_NOT_FOUND_EXCEPTION, email));
        }

        LOG.info("Found user {}", user);
        return user;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
