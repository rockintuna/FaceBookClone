package com.spring.clone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestApiExceptionHandler {


    @ExceptionHandler(value = {CloneException.class})
    public ResponseEntity<Map<String, Object>> handleApiRequestException(CloneException ex) {
        Map<String, Object> result = new HashMap<>();

        result.put("statusCode", ex.getErrorCode().getHttpStatus().value());
        result.put("responseMessage", ex.getErrorCode().getMessage());
        return new ResponseEntity<>(result, ex.getErrorCode().getHttpStatus());
    }
}
