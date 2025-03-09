package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.utils.EmailVaildator;
import com.example.demo.utils.PasswordVaildator;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.RepaintManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request , HttpSession session) {
        
        Optional<UserBean> userOpt = userService.loginCheck(request);

        if(userOpt.isEmpty()) {
            throw new ApiException(Map.of("email", "帳號或密碼錯誤"));
        }

        Map<String,Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("errors", null);
        response.put("data", Map.of(
            "id", userOpt.get().getId(),
            "email", userOpt.get().getEmail(),
            "created_at", userOpt.get().getCreated_at()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        
        Optional<Map<String,String>> errors = userService.registerCheck(request);
        
        if(errors.isPresent()) {
            throw new ApiException(errors.get());
        }

        UserBean user = userService.registerSave(request);

        Map<String,Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("errors", null);
        response.put("data", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "created_at", user.getCreated_at()
        ));

        return ResponseEntity.ok(response);
    }
    

}
