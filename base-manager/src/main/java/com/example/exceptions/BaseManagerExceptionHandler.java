package com.example.exceptions;

import com.example.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class BaseManagerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BaseManagerExceptionHandler.class);

    private final HttpServletRequest request;

    public BaseManagerExceptionHandler(HttpServletRequest request) {
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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorMessage> handleResourceAccessException(ResourceAccessException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorMessage> handleInternalServerErrorException(InternalServerErrorException ex) {
        LOG.error(ex.getMessage());
        return new ResponseEntity<>(buildErrorMessage(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorMessage> handleHttpServerErrorException(HttpServerErrorException ex) {
        String errorMessageAsString = formatHttpServerErrorExceptionErrorMessage(ex.getMessage());
        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(errorMessageAsString, ErrorMessage.class);
        LOG.error(errorMessage.toString());
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String formatHttpServerErrorExceptionErrorMessage(String message) {
        /* Remove the 'XXX : "' in the beginning and the quotation marks in the end of the message */
        return message.substring(7, message.length() - 1);
    }

    private ErrorMessage buildErrorMessage(RuntimeException ex) {
        String errorType = ex.getClass().getSimpleName().substring(0, ex.getClass().getSimpleName().length() - 9);
        return new ErrorMessage(errorType,
                ex.getMessage(),
                request.getRequestURI());
    }

}
