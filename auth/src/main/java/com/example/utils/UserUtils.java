package com.example.utils;

import com.example.models.User;

public abstract class UserUtils {

    public static User updateUser(User currentUser, User newUser) {
        currentUser.setEmail(newUser.getEmail());
        currentUser.setUsername(newUser.getUsername());
        currentUser.setPassword(newUser.getPassword());

        return currentUser;
    }

}
