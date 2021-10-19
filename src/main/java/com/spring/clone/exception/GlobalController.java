package com.spring.clone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        result.put("statusCode", HttpStatus.FORBIDDEN.value());
        return result;
    }

    @ExceptionHandler
    public Map<String, Object> authenticationServiceExceptionHandler(AuthenticationServiceException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        return result;
    }

    @ExceptionHandler
    public Map<String, Object> postNotFoundExceptionHandler(PostNotFoundException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.NOT_FOUND.value());
        return result;
    }

    @ExceptionHandler
    public Map<String, Object> commentNotFoundExceptionHandler(CommentNotFoundException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.NOT_FOUND.value());
        return result;
    }

    @ExceptionHandler
    public Map<String, Object> usernameNotFoundExceptionHandler(UsernameNotFoundException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.NOT_FOUND.value());
        return result;
    }

    @ExceptionHandler
    public Map<String, Object> badArgumentExceptionHandler(BadArgumentException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.BAD_REQUEST.value());
        return result;
    }
}
