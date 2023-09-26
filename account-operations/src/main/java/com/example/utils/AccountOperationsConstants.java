package com.example.utils;

public abstract class AccountOperationsConstants {

    public static final String CUSTOMER_NOT_FOUND_EXCEPTION = "Customer with email %s was not found!";

    public static final String CUSTOMER_ALREADY_EXISTS_EXCEPTION = "Customer with email %s already exists!";

    public static final String CUSTOMER_EMAIL_DIFFERENT_FROM_TOKEN_EMAIL = "There was an error in the request while validating the token's email!";

    public static final String CUSTOMER_ALREADY_HAS_CURRENT_ACCOUNT = "Customer with email %s already has a current account!";

    public static final String CUSTOMER_DOES_NOT_HAVE_CURRENT_ACCOUNT = "Customer with email %s does not have a current account!";

    public static final String INSUFFICIENT_FUNDS_TO_WITHDRAW = "Insufficient funds! The requested withdrawal amount exceeds the available balance in your account!";

    public static final String WRONG_CURRENT_ACCOUNT_NUMBER = "The Current Account you are trying to access does not belong to you!";
}
