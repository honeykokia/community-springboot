package com.example.demo.utils;

public class PasswordVaildator {
    private static final String PASSWORD_PATTERN="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,12}$";

    public static boolean isValidPassword(String password) {
        return password.matches(PASSWORD_PATTERN);
    }

}
