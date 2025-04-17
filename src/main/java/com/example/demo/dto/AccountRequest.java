package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AccountRequest {
    private String name;
    private byte type;
    private String description;
    private String image;
    private Long initial_amount;
    private Boolean is_public;
    private LocalDateTime created_at;
}
