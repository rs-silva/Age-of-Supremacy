package com.example.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class AuthExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        String errorMessage = formatHttpClientErrorExceptionErrorMessage(ex.getMessage());
        LOG.error(errorMessage);
        return new ResponseEntity<>(errorMessage, ex.getStatusCode());
    }

    private String formatHttpClientErrorExceptionErrorMessage(String message) {
        /* Remove the 'XXX : "' in the beginning and the quotation marks in the end of the message */
        return message.substring(7, message.length() - 1);
    }
}
