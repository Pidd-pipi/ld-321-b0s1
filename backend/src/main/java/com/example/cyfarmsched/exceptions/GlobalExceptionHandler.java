package com.example.cyfarmsched.exceptions;

import com.example.cyfarmsched.logger.AppLogger;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = AppLogger.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessException ex) {
        log.warn("business error {}: {}", ex.getCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("code", ex.getCode(), "message", ex.getMessage()));
    }
}
