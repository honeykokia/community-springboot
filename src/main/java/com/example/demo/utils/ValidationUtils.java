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

    public static void comparePassword(Map<String,String> errors , String fieldName , String userPassword, String inputPassword , String message){
        if((userPassword.isBlank() || inputPassword.isBlank()) || !userPassword.equals(inputPassword)){
            errors.put(fieldName, message);
        }
    }
    
    public static void compareinputAndDbPassword(Map<String,String> errors , String fieldName , String userPassword, String inputPassword , String message){
        if((userPassword.isBlank() || inputPassword.isBlank()) || !userPassword.equals(inputPassword) || !HashUtil.matches(inputPassword, userPassword)){
            errors.put(fieldName, message);
        }
    }

    public static void passwordVaildator(Map<String,String> errors , String fieldName , String password , String message){
        if(!PasswordVaildator.isValidPassword(password)){
            errors.put(fieldName, message);
        }
    }

    public static void checkIsNegative(Map<String,String> errors , String fieldName , Long value , String message){
        if( value != null && value < 0){
            errors.put(fieldName, message);
        }
    }   

}
