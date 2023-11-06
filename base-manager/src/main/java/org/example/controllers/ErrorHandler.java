package org.example.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.example.exceptions.ErrorMessage;
import org.example.utils.JwtAccessTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorHandler implements ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    public ErrorHandler(JwtAccessTokenUtils jwtAccessTokenUtils) {
        this.jwtAccessTokenUtils = jwtAccessTokenUtils;
    }

    @RequestMapping("/error")
    public ResponseEntity<ErrorMessage> handleError(HttpServletRequest request) {
        Object ERROR_EXCEPTION = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String error = ERROR_EXCEPTION != null ? ERROR_EXCEPTION.toString() : "";

        Object ERROR_MESSAGE = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String message = ERROR_MESSAGE != null ? ERROR_MESSAGE.toString() : "";

        String requestURI = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
        int status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        ErrorMessage errorMessage = new ErrorMessage(error, message, requestURI);

        String userEmail;
        try {
            userEmail = jwtAccessTokenUtils.retrieveEmailFromRequestToken();
        }
        catch (Exception ex) {
            userEmail = "not logged in";
        }

        LOG.error("Error from user {} : {}", userEmail , errorMessage);

        return ResponseEntity.status(status).body(errorMessage);
    }

}
