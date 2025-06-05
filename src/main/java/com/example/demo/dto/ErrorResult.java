package com.example.demo.dto;

import java.util.HashMap;
import java.util.Map;

public class ErrorResult {
    private Map<String, String> errors = new HashMap<>();

    public void add (String key, String message){
        this.errors.put(key, message);
    }

    public boolean hasErrors(){
        return !this.errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
