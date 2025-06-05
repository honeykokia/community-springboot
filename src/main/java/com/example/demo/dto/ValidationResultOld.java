package com.example.demo.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Data;

@Data
public class ValidationResultOld<T> {
    private T target;
    private Optional<Map<String,String>> errors;

    public static <T> ValidationResultOld<T> failFast(String key , String message) {
        ValidationResultOld<T> result = new ValidationResultOld<>();
        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put(key, message);
        result.setTarget(null);
        result.setErrors(Optional.of(errorMap));
        return result;
    }
}   
