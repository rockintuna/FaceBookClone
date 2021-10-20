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
    public ResponseEntity<Map<String, Object>> accessDeniedExceptionHandler(AccessDeniedException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> authenticationServiceExceptionHandler(AuthenticationServiceException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> postNotFoundExceptionHandler(PostNotFoundException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> commentNotFoundExceptionHandler(CommentNotFoundException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> usernameNotFoundExceptionHandler(UsernameNotFoundException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> badArgumentExceptionHandler(BadArgumentException exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("responseMessage", exception.getMessage());
        result.put("statusCode", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
