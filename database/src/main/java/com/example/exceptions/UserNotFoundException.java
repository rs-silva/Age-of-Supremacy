package com.example.exceptions;

import com.example.utils.DatabaseConstants;
import java.text.MessageFormat;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(MessageFormat.format(DatabaseConstants.USER_NOT_FOUND_EXCEPTION, username));
    }
}
