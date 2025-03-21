package com.example.demo.response;

import java.util.HashMap;

import lombok.Data;

@Data
public class SuccessResponse {
    private final String status ="success";
    private Object data;

    public SuccessResponse(Object data) {
        this.data = data != null ? data : new HashMap<>();
    }

}
