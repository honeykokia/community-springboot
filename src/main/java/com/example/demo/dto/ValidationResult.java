package com.example.demo.dto;

import java.util.Map;
import java.util.Optional;

import lombok.Data;

@Data
public class ValidationResult<T> {
    private T target;
    private Optional<Map<String,String>> errors;
}   
