package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CaptchaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final CaptchaService CaptchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.CaptchaService = captchaService;
    }

    @GetMapping
    public ResponseEntity<?> getCaptcha(){
        return CaptchaService.getCaptcha();
    }
    
}
