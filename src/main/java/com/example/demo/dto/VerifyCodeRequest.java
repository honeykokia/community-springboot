package com.example.demo.dto;

import lombok.Data;

@Data
public class VerifyCodeRequest {
    private String verifyToken;
    private String code;
}
