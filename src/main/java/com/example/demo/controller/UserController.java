package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.response.SuccessResponse;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {

        Optional<Map<String, String>> errors = userService.loginCheck(request);

        if (errors.isPresent()) {
            throw new ApiException(errors.get());
        }

        UserBean user = userService.findByEmail(request.getEmail()).get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        SuccessResponse response = new SuccessResponse(Map.of(
                "token", token,
                "image", user.getImage(),
                "id", user.getId(),
                "email", user.getEmail(),
                "created_at", user.getCreated_at()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        Optional<Map<String, String>> errors = userService.registerCheck(request);

        if (errors.isPresent()) {
            throw new ApiException(errors.get());
        }

        return userService.registerSave(request);
    }

    @PostMapping("/member")
    public ResponseEntity<?> member() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userService.getMemberProfile(email);
    }

    @PutMapping("/member")
    public ResponseEntity<?> memberSave(
        @RequestPart("data") MemberRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) {
        
        return userService.uploadMemberProfile(request, file);
    }
}
