package com.example.demo.mq.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.EmailMessage;

@Service
public class UserProducer {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMail(String email, String verifyLink) {
        // 發送郵件的邏輯
        EmailMessage message = new EmailMessage(email, verifyLink);
        amqpTemplate.convertAndSend("user.exchange", "user.sendmail", message);
    }

}
