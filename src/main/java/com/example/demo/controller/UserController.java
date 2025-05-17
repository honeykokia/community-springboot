package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.EmailRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.dto.VerifyCodeRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${front.end.host}")
    private String frontEndHost;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
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
            String message = result.getErrors().get().get("email");
            message = URLEncoder.encode(message, StandardCharsets.UTF_8);
            return new RedirectView(frontEndHost + "/projectA/verifyFail?message=" + message); // Redirect to a different URL
        }

        return new RedirectView(frontEndHost + "/projectA/verifySuccess"); // Redirect to a different URL
    }

    @PostMapping("/resendMail")
    public ResponseEntity<?> resendMail(@RequestBody Map<String,String> req) {
        String email = req.get("email");
        return userService.resendMail(email);
    }

    @GetMapping("/member")
    public ResponseEntity<?> member() {
        return userService.getMemberProfile();
    }

    @PutMapping("/member")
    public ResponseEntity<?> memberSave(
        @RequestPart("data") MemberRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) {
        //TODO: Wait add vaildation

        return userService.updateMemberProfile(request, file);
    }

    @PatchMapping("/member/password")
    public ResponseEntity<?> memberPasswordUpdate(@RequestBody PasswordRequest request) {
        ValidationResult<UserBean> result = userService.updatePasswordCheck(request);

        if (result.getErrors().isPresent()) {
            throw new ApiException(result.getErrors().get());
        }

        return userService.updatePassword(result.getTarget(), request.getPassword());
    }


    /*
     * TODO:
     * 1. 前端傳送email進來
     * 2. 資料庫確認該email是否存在
     * 3. 若存在則寄送驗證碼，並通知前端至信箱收取驗證碼
     * 4. 同時傳送token給前端做後續驗證
     * 4. 若不存在則回傳錯誤訊息
     */
    @PostMapping("/forgetPassword/request")
    public ResponseEntity<?> forgetPassword(@RequestBody EmailRequest request) {
        //TODO: process POST request
        String email = request.getEmail();
        return userService.forgetPassword(email);
    }

    /*
     * TODO:
     * 1. 確認驗證碼是否正確
     * 2. 若正確則回傳token，並通知前端進入重設密碼頁面
     * 3. 若不正確則回傳錯誤訊息
     */
    @PostMapping("/forgetPassword/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        //TODO: process POST request
        
        return userService.verifyCode(request);
    }
    
    /*
     * TODO:
     * 1. 接收前端的token和使用者新密碼輸入的新密碼
     * 2. 驗證token是否正確
     * 3. 若正確則更新密碼，並通知前端密碼已重設成功
     * 4. 若不正確則回傳錯誤訊息
     */

    @PutMapping("/forgetPassword/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        return userService.resetPassword(request);
    }

    

}
