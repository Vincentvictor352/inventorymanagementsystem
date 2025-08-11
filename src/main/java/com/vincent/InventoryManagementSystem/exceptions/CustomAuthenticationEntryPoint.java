package com.vincent.InventoryManagementSystem.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;


@Component
@Builder
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException, java.io.IOException {

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.UNAUTHORIZED.value(),
                "message", authException.getMessage(),
                "timestamp", LocalDateTime.now()
        );

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
//    private  ObjectMapper objectMapper;
//
//
//    @Override
//    public void commence(HttpServletRequest request,
//                         HttpServletResponse response,
//                         AuthenticationException authException)
//            throws IOException, ServletException, java.io.IOException {
//        Response errorResponse = ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED.value())
//                .message(authException.getMessage())
//                .build();
//
//        response.setContentType("application/json");
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
//
//    }
}