package com.example.services;

import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.AuthConstants;
import com.example.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public User updateUser(User currentUser, User updatedUser) {
        User newUser = UserUtils.updateUser(currentUser, updatedUser);
        return userRepository.save(newUser);
    }
}
