package com.example.demo.exception;

import java.util.Map;

public class ApiException extends RuntimeException {

    private final String status;
    private final Map<String,String> errors;
    private final String data;

    public ApiException(Map<String,String> errors) {
        super("請檢查輸入的資料");
        this.status = "error";
        this.errors = errors;
        this.data = null; //失敗時 data為null
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getData() {
        return data;
    }

}
