package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private LocalDate birthday;
    private String gender;
    private String email;
    private String password;
    private String confirmPassword;
}
