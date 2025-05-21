package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AccountRequest {
    private String name;
    private byte type;
    private String description;
    private String image;
    private Long initialAmount;
    private Boolean isPublic;
    private LocalDateTime createdAt;
}
