package com.example.demo.service;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.example.demo.response.SuccessResponse;

@Service
public class CaptchaService {

    public ResponseEntity<?> getCaptcha(){
        int width = 120;
        int height = 40;
        BufferedImage captcha = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = captcha.createGraphics();
        Random r = new Random();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0 ; i < 4 ; i++){
            char c = chars.charAt(r.nextInt(chars.length()));
            code.append(c);
            g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
            g.drawString(String.valueOf(c), 20 + i * 20, 28);
        }

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

}
