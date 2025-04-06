package com.example.demo.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;

@Data
public class ValidationResult<T> {
    private T target;
    private Optional<Map<String,String>> errors;

    public static <T> ValidationResult<T> failFast(String key , String message) {
        ValidationResult<T> result = new ValidationResult<>();
        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put(key, message);
        result.setTarget(null);
        result.setErrors(Optional.of(errorMap));
        return result;
    }
}   
