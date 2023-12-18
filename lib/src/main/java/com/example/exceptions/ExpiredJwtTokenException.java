package com.example.exceptions;

public class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException(String message) {
        super(message);
    }
}
