package com.example.controllers;

import com.example.exceptions.ErrorMessage;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorHandler implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorMessage> handleError(HttpServletRequest request) {
        Object ERROR_EXCEPTION = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String error = ERROR_EXCEPTION != null ? ERROR_EXCEPTION.toString() : "";

        Object ERROR_MESSAGE = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String message = ERROR_MESSAGE != null ? ERROR_MESSAGE.toString() : "";

        String requestURI = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
        int status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        ErrorMessage errorMessage = new ErrorMessage(error, message, requestURI);

        return ResponseEntity.status(status).body(errorMessage);
    }

}
