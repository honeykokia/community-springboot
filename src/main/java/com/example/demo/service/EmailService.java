package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    public void sendEmailByRegister(String to, String verifyLink) {
        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("請驗證你的帳號");
        message.setText("您好，請點擊以下連結以啟用您的帳號：\n" + verifyLink);

        // Send the email
        emailSender.send(message); 
    }

    public void sendEmailByForgetPassword(String to, String code) {
        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("重設密碼驗證碼");
        message.setText("您的驗證碼是：" + code + "\n請在 5 分鐘內使用此驗證碼重設密碼。");

        // Send the email
        emailSender.send(message); 
    }
}
