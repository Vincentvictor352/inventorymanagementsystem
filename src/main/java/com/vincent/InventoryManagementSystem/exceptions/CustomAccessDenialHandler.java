package com.vincent.InventoryManagementSystem.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class CustomAccessDenialHandler implements AccessDeniedHandler {

    private  ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        // Create a map or object for the body
        Map<String, Object> body = Map.of(
                "status", HttpStatus.FORBIDDEN.value(),
                "message", accessDeniedException.getMessage(),
                "timestamp", LocalDateTime.now()
        );

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
//    @Override
//    public void handle(HttpServletRequest request,
//                       HttpServletResponse response,
//                       AccessDeniedException accessDeniedException)
//            throws IOException, ServletException {
//        Response errorResponse = ResponseEntity
//                .status(HttpStatus.FORBIDDEN.value())
//                .message(accessDeniedException.getMessage())
//                .build();
//
//        response.setContentType("application/json");
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
//
//    }
}
