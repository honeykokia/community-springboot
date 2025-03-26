package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobaExceptionHandler {
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String,Object>> handleApiException(ApiException e){
        Map<String,Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("errors", e.getErrors());
        response.put("data",null);

        return ResponseEntity.badRequest().body(response);
    }

}
