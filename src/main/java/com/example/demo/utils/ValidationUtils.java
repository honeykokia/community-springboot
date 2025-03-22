package com.example.demo.utils;

import java.util.Map;

public class ValidationUtils {

    public static void checkIsBlank(Map<String, String> errors, String fieldName, String value, String message) {
        if (value == null || value.isBlank()) {
            errors.put(fieldName, message);
        }
    }

    public static void checkNull(Map<String,String> errors , String fieldName, Object value , String message){
        if(value == null){
            errors.put(fieldName, message);
        }
    }

    public static void comparePassword(Map<String,String> errors , String fieldName , String onePassword, String twoPassword , String message){
        if((onePassword.isBlank() || twoPassword.isBlank()) || !onePassword.equals(twoPassword)){
            errors.put(fieldName, message);
        }
    }

    public static void passwordVaildator(Map<String,String> errors , String fieldName , String password , String message){
        if(!PasswordVaildator.isValidPassword(password)){
            errors.put(fieldName, message);
        }
    }

}
