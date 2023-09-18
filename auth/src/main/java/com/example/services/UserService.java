package com.example.services;

import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.AuthConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUserToDatabase(User newUser) {
        User user = userRepository.findByEmail(newUser.getEmail());

        if (user != null) {
            throw new ResourceAlreadyExistsException(
                    String.format(AuthConstants.USER_ALREADY_EXISTS_EXCEPTION, newUser.getEmail()));
        }

        userRepository.save(newUser);
        LOG.info("Added user {} to database!", newUser.getEmail());
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException(
                    String.format(AuthConstants.USER_NOT_FOUND_EXCEPTION, email));
        }

        LOG.info("Found user = {}", user);
        return user;
    }
}
