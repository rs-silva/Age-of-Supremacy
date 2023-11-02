package com.example.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class AuthExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthExceptionHandler.class);

    private final HttpServletRequest request;

    public AuthExceptionHandler(HttpServletRequest request) {
        this.request = request;
    }

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

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedException(InvalidCredentialsException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorMessage> handleForbiddenException(ForbiddenException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorMessage> handleRefreshTokenException(RefreshTokenException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.FORBIDDEN);
    }

    private ErrorMessage buildErrorMessage(RuntimeException ex) {
        String errorType = ex.getClass().getSimpleName().substring(0, ex.getClass().getSimpleName().length() - 9);
        return new ErrorMessage(errorType,
                ex.getMessage(),
                request.getRequestURI());
    }
}
