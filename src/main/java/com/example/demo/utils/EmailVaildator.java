package com.example.demo.utils;


public class EmailVaildator {

    private static final String EMAIL_STRING="^[a-zA-Z0-9]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_STRING);
    }

}
