package com.example.config;

import com.example.exceptions.ErrorMessage;
import com.example.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(403);

        ErrorMessage error = new ErrorMessage("ForbiddenException", "You have no permissions to access this resource.");

        res.getWriter().write(JsonUtils.asJsonString(error));
    }
}
