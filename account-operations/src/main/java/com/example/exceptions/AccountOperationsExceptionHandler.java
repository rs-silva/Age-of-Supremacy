package com.example.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountOperationsExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AccountOperationsExceptionHandler.class);

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

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorMessage> handleInternalServerErrorException(InternalServerErrorException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.BAD_REQUEST);
    }

    private ErrorMessage buildErrorMessage(RuntimeException ex) {
        return new ErrorMessage(ex.getClass().getSimpleName(), ex.getMessage());
    }

}
