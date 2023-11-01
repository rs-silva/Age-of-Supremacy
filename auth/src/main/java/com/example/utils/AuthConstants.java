package com.example.utils;

public abstract class AuthConstants {

    public static final String ACCESS_DENIED_TO_RESOURCE = "User %s attempted to access resource %s without the required permissions.";

    public static final String USER_WITH_EMAIL_NOT_FOUND_EXCEPTION = "User with email %s was not found!";

    public static final String USER_WITH_ID_NOT_FOUND_EXCEPTION = "User with id %s was not found!";

    public static final String USER_ALREADY_EXISTS_EXCEPTION = "User with email %s already exists!";

    public static final String WRONG_LOGIN_CREDENTIALS = "Invalid login credentials!";

    public static final String CUSTOMER_EMAIL_DIFFERENT_FROM_TOKEN_EMAIL = "There was an error in the request while validating the token's email!";

    public static final String USER_EMAIL_FROM_GIVEN_ID_DOES_NOT_MATCH_EMAIL_FROM_REQUEST_PARAM = "There was an error in the request while validating the user's email!";

    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token has expired. Please login again.";

    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token was not found. Please login again";

    public static final String REFRESH_TOKEN_DOES_NOT_BELONG_TO_USER = "The refresh token provided does not belong to the user with the provided email.";
}