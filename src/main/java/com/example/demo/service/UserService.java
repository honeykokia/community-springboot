package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.EmailVaildator;
import com.example.demo.utils.PasswordVaildator;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public Optional<Map<String,String>> loginCheck(LoginRequest request) {

        HashMap<String,String> errors = new HashMap<String,String>();
        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());

        if(userOpt.isEmpty()) {
            errors.put("email", "帳號或密碼錯誤");
        }

        if(userOpt.isPresent() && !userOpt.get().getPassword().equals(request.getPassword())) {
            errors.put("password", "帳號或密碼錯誤");
        }

        if(request.getEmail() == "" || request.getEmail() == null) {
            errors.put("email", "請輸入信箱");
        }

        if(request.getPassword() == "" || request.getPassword() == null) {
            errors.put("password", "請輸入密碼");
        }

        if(errors.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(errors);
    }

    public Optional<Map<String,String>> registerCheck(RegisterRequest request) {

        HashMap<String,String> errors = new HashMap<String,String>();

        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());


        if(request.getName() == null || request.getName().isBlank()){
            errors.put("name", "請輸入姓名");
        }

        if(request.getBirthday() == null || request.getBirthday().toString().isBlank()){
            errors.put("birthday", "請輸入生日");
        }

        if(request.getGender() == "" || request.getGender() == null) {
            errors.put("gender", "請輸入性別");
        }

        if(request.getEmail() == "" || request.getEmail() == null) {
            errors.put("email", "請輸入信箱");
        }

        if(request.getPassword() == "" || request.getPassword() == null) {
            errors.put("password", "請輸入密碼");
        }

        if(request.getConfirmPassword() == "" || request.getConfirmPassword() == null) {
            errors.put("confirmPassword", "請輸入確認密碼");
        }

        if(userOpt.isPresent()) {
            errors.put("email", "此信箱已被註冊");
        }

        if(!EmailVaildator.isValidEmail(request.getEmail())) {
            errors.put("email", "信箱格式不符合");
        }

        if(request.getBirthday().isAfter(java.time.LocalDate.now())) {
            errors.put("birthday", "生日不可大於今天");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            errors.put("confirmPassword", "確認密碼不一致");
        }

        if(!PasswordVaildator.isValidPassword(request.getPassword())) {
            errors.put("password", "密碼格式不符合，必須要有大小寫英文及數字，且長度要大於8小於12");
        }

        if(errors.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(errors);
    }

    public UserBean registerSave(RegisterRequest request) {
        UserBean user = new UserBean();
        user.setName(request.getName());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setImage("/uploads/defaultAvatar.jpg");
        user.setRole((byte) 1);
        user.set_active(true);
        user.setCreated_at(java.time.LocalDateTime.now());

        userRepo.save(user);
        return user;
    }

    public Optional<UserBean> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
