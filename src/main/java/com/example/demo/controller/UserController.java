package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${front.end.host}")
    private String frontEndHost;

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

    @GetMapping("/verify")
    public RedirectView verify(@RequestParam String token) {
        ValidationResult<UserBean> result = userService.verifyEmail(token);
        if (result.getErrors().isPresent()) {
            String message = result.getErrors().get().get("token");
            message = URLEncoder.encode(message, StandardCharsets.UTF_8);
            return new RedirectView(frontEndHost + "/verifyFail?message=" + message); // Redirect to a different URL
        }

        return new RedirectView(frontEndHost + "/verifySuccess"); // Redirect to a different URL
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
