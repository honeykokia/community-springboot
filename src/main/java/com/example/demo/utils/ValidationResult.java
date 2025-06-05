package com.example.demo.utils;

import java.time.LocalDate;

import com.example.demo.dto.ErrorResult;

public class ValidationResult {

    private ErrorResult errors = new ErrorResult();

    public void checkIsBlank(String fieldName, String value, String message) {
        if (value == null || value.isBlank()) {
            errors.add(fieldName, message);
        }
    }

    public void checkNull(String fieldName, Object value , String message){
        if(value == null){
            errors.add(fieldName, message);
        }
    }

    public void comparePassword(String fieldName , String userPassword, String inputPassword , String message){
        if((userPassword.isBlank() || inputPassword.isBlank()) || !userPassword.equals(inputPassword)){
            errors.add(fieldName, message);
        }
    }
    
    public void compareinputAndDbPassword(String fieldName , String userPassword, String inputPassword , String message){
        if((userPassword.isBlank() || inputPassword.isBlank()) || !HashUtil.matches(inputPassword, userPassword)){
            errors.add(fieldName, message);
        }
    }

    public void passwordVaildator(String fieldName , String password , String message){
        if(!PasswordVaildator.isValidPassword(password)){
            errors.add(fieldName, message);
        }
    }

    public void checkIsNegative(String fieldName , Long value , String message){
        if( value != null && value < 0){
            errors.add(fieldName, message);
        }
    }
    public void checkMailformat(String fieldName, String email, String message) {
        if (email == null || !EmailVaildator.isValidEmail(email)) {
            errors.add(fieldName, message);
        }
    }
    public void checkBirthdayFormat(String fieldName, LocalDate birthday, String message) {
        if (birthday == null || !birthday.isAfter(java.time.LocalDate.now())) {
            errors.add(fieldName, message);
        }
    }

    public boolean hasErrors(){
        return errors.hasErrors();
    }

    public ErrorResult getErrors() {
        return errors;
    }

    public ErrorResult failFast(String fieldName, String message){
        errors.add(fieldName, message);
        return errors;
    }
}
