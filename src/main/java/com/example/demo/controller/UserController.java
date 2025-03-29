package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.exception.ApiException;
import com.example.demo.service.UserService;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        ValidationResult<UserBean> result = userService.loginCheck(request);

        if (result.getErrors().isPresent()) {
            throw new ApiException(result.getErrors().get());
        }

        UserBean user = result.getTarget();

        return userService.loginCreateToken(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        ValidationResult<UserBean> result = userService.registerCheck(request);
    
        if (result.getErrors().isPresent()) {
            throw new ApiException(result.getErrors().get());
        }

        return userService.registerSave(request);
    }

    @PostMapping("/member")
    public ResponseEntity<?> member() {
        return userService.getMemberProfile();
    }

    @PutMapping("/member")
    public ResponseEntity<?> memberSave(
        @RequestPart("data") MemberRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) {
        
        return userService.updateMemberProfile(request, file);
    }

    @PatchMapping("/member")
    public ResponseEntity<?> memberPasswordUpdate(@RequestBody PasswordRequest request) {
        ValidationResult<UserBean> result = userService.updatePasswordCheck(request);

        if (result.getErrors().isPresent()) {
            throw new ApiException(result.getErrors().get());
        }

        return userService.updatePassword(result.getTarget(), request.getPassword());
    }
    
}
