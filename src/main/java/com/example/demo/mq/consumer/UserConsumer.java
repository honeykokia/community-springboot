package com.example.demo.mq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.demo.dto.EmailMessage;
import com.example.demo.service.EmailService;

@Component
public class UserConsumer {

    private final EmailService emailService;

    public UserConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "user.queue")
    public void handleSendMail(EmailMessage msg) {
        emailService.sendEmailByRegister(msg.getEmail(), msg.getVerifyUrl());
    }
}
