package com.example.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EventManagerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EventManagerExceptionHandler.class);

    private final HttpServletRequest request;

    public EventManagerExceptionHandler(HttpServletRequest request) {
        this.request = request;
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

    @ExceptionHandler(ExpiredJwtTokenException.class)
    public ResponseEntity<ErrorMessage> handleExpiredJwtTokenException(ExpiredJwtTokenException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorMessage> handleInternalServerErrorException(InternalServerErrorException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorMessage buildErrorMessage(RuntimeException ex) {
        String errorType = ex.getClass().getSimpleName().substring(0, ex.getClass().getSimpleName().length() - 9);
        return new ErrorMessage(errorType,
                ex.getMessage(),
                request.getRequestURI());
    }

}
