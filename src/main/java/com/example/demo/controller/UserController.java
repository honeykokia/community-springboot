package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/login")
    public UserBean Login(@RequestBody LoginRequest request , HttpSession session) {
        
        UserBean userBean = userRepo.findByEmail(request.getEmail());

        return userBean;
    }

}
