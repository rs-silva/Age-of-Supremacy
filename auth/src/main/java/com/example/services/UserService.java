package com.example.services;

import com.example.exceptions.ResourceAlreadyExistsException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.AuthConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ResourceNotFoundException(
                    String.format(AuthConstants.USER_NOT_FOUND_EXCEPTION, username));
        }

        LOG.info("Found user = {}", user);
        return user;
    }

    public void addUserToDatabase(User newUser) {
        User user = userRepository.findByUsername(newUser.getUsername());

        if (user != null) {
            throw new ResourceAlreadyExistsException(
                    String.format(AuthConstants.USER_ALREADY_EXISTS_EXCEPTION, newUser.getUsername()));
        }

        LOG.info("Added user {} to database!", newUser.getUsername());
        userRepository.save(newUser);
    }
}
