package com.spring.clone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalController {

    @ExceptionHandler
    public Map<String, Object> accessDeniedExceptionHandler(AccessDeniedException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.FORBIDDEN);
        return result;
    }
}
