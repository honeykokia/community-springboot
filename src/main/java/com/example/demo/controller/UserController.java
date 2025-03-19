package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request , HttpSession session) {
        
        Optional<Map<String,String>> errors = userService.loginCheck(request);

        if(errors.isPresent()) {
            throw new ApiException(errors.get());
        }

        UserBean user = userService.findByEmail(request.getEmail()).get();

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        Map<String,Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("token", token);
        response.put("data", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "created_at", user.getCreated_at()
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
        // response.put("errors", null);
        response.put("data", Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "created_at", user.getCreated_at()
        ));

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/member")
    public ResponseEntity<?> member() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Optional<UserBean> optUser = userService.findByEmail(email);
        UserBean userBean = new UserBean();
        if(optUser.isPresent()){
            userBean = optUser.get();
        }
        
        if (userBean.getGender().equals("male")) {
            userBean.setGender("男");
        } else if (userBean.getGender().equals("female")) {
            userBean.setGender("女");
        } else {
            userBean.setGender("其他");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("status", "success");
        map.put("data",userBean);

        return ResponseEntity.ok(map);
    }
    
}
