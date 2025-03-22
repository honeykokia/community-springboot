package com.example.demo.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.EmailVaildator;
import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.ValidationUtils;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;


    public ValidationResult<UserBean> loginCheck(LoginRequest request) {

        ValidationResult<UserBean> result = new ValidationResult<UserBean>();
        HashMap<String,String> errors = new HashMap<String,String>();
        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());

        ValidationUtils.checkIsBlank(errors, "email", request.getEmail(), "請輸入信箱");
        ValidationUtils.checkIsBlank(errors, "password", request.getPassword(), "請輸入密碼");
        if(userOpt.isEmpty()) {
            errors.put("email", "帳號或密碼錯誤");
            result.setErrors(Optional.of(errors));
            result.setTarget(null);
            return result;
        }

        UserBean user = userOpt.get();
        ValidationUtils.comparePassword(errors, "password", user.getPassword(), request.getPassword(), "帳號或密碼錯誤");
    
        if(errors.isEmpty()){
            result.setErrors(Optional.empty());
        }else{
            result.setErrors(Optional.of(errors));
        }
        result.setTarget(user);

        return result;
    }

    public ValidationResult<UserBean> registerCheck(RegisterRequest request) {

        ValidationResult<UserBean> result = new ValidationResult<UserBean>();
        HashMap<String,String> errors = new HashMap<String,String>();
        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());

        
        if(userOpt.isPresent()) {
            errors.put("email", "此信箱已被註冊");
        }

        ValidationUtils.checkIsBlank(errors, "name", request.getName(), "請輸入姓名");
        ValidationUtils.checkNull(errors, "birthday", request.getBirthday(), "請輸入生日");
        ValidationUtils.checkIsBlank(errors,"gender", request.getGender(), "請輸入性別");
        ValidationUtils.checkIsBlank(errors, "email", request.getEmail(), "請輸入信箱");
        ValidationUtils.checkIsBlank(errors, "password", request.getPassword(), "請輸入密碼");
        ValidationUtils.checkIsBlank(errors, "confirmPassword", request.getConfirmPassword(), "請輸入確認密碼");
        ValidationUtils.comparePassword(errors, "confirmPassword", request.getPassword(), request.getConfirmPassword(), "確認密碼不一致");
        ValidationUtils.passwordVaildator(errors, "password", request.getPassword(), "密碼格式不符合，必須要有大小寫英文及數字，且長度要大於8小於12");

        if(!EmailVaildator.isValidEmail(request.getEmail())) {
            errors.put("email", "信箱格式不符合");
        }

        if(request.getBirthday().isAfter(java.time.LocalDate.now())) {
            errors.put("birthday", "生日不可大於今天");
        }

        if(errors.isEmpty()){
            result.setErrors(Optional.empty());
        }else{
            result.setErrors(Optional.of(errors));
        }
        result.setTarget(null);

        return result;
    }

    public ValidationResult<UserBean> updatePasswordCheck(String email , PasswordRequest request){

        ValidationResult<UserBean> result = new ValidationResult<UserBean>();
        HashMap<String,String> errors = new HashMap<String,String>();
        Optional<UserBean> userOpt = userRepo.findByEmail(email);

        if(userOpt.isEmpty()) {
            errors.put("oldPassword", "密碼更新失敗");
            result.setErrors(Optional.of(errors));
            result.setTarget(null);
            return result;
        }

        UserBean user = userOpt.get();
        ValidationUtils.checkIsBlank(errors, "oldPassword", request.getOldPassword(), "請輸入舊密碼");
        ValidationUtils.checkIsBlank(errors, "password", request.getPassword(), "請輸入新密碼");
        ValidationUtils.checkIsBlank(errors, "confirmPassword", request.getConfirmPassword(), "請輸入確認密碼");
        ValidationUtils.comparePassword(errors, "oldPassword", user.getPassword(), request.getOldPassword(), "舊密碼錯誤");
        ValidationUtils.comparePassword(errors, "confirmPassword", request.getPassword(), request.getConfirmPassword(), "確認密碼不一致");
        ValidationUtils.passwordVaildator(errors, "password", request.getPassword(), "密碼格式不符合，必須要有大小寫英文及數字，且長度要大於8小於12");

        if (errors.isEmpty()) {
            result.setErrors(Optional.empty());
        }else{
            result.setErrors(Optional.of(errors));
        }
        result.setTarget(user);

        return result;

    }

    public ResponseEntity<?> loginCreateToke(UserBean user) {

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        SuccessResponse response = new SuccessResponse(Map.of(
                "token", token,
                "image", user.getImage(),
                "id", user.getId(),
                "email", user.getEmail(),
                "created_at", user.getCreated_at()));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> registerSave(RegisterRequest request) {
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

        SuccessResponse response = new SuccessResponse(Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "created_at", user.getCreated_at()));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getMemberProfile(String email) {
        Optional<UserBean> optUser = userRepo.findByEmail(email);
        UserBean userBean = new UserBean();
        if(optUser.isEmpty()){
            throw new ApiException(Map.of("error", "查無此會員"));
        }

        userBean = optUser.get();
        if (userBean.getGender().equals("male")) {
            userBean.setGender("男");
        } else if (userBean.getGender().equals("female")) {
            userBean.setGender("女");
        } else {
            userBean.setGender("其他");
        }

        SuccessResponse response = new SuccessResponse(userBean);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateMemberProfile(MemberRequest request, MultipartFile file) {
        Optional<UserBean> optUser = userRepo.findByEmail(request.getEmail());
        UserBean userBean = new UserBean();
        if(optUser.isEmpty()){
            throw new ApiException(Map.of("general", "帳號更新失敗"));
        }
        userBean = optUser.get();
        userBean.setName(request.getName());
        userBean.setBirthday(request.getBirthday());
        userBean.setPassword(request.getPassword());
    
        if(file !=null && !file.isEmpty()){
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            String fileName = file.getOriginalFilename();
            File saveFile = new File(uploadDir + fileName);
            try {
                file.transferTo(saveFile);
                userBean.setImage("/uploads/" + fileName);
            } catch (Exception e) {
                throw new ApiException(Map.of("general", "上傳圖片失敗"));
            }
        }
        userRepo.save(userBean);

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updatePassword(UserBean user , String password) {
        user.setPassword(password);
        userRepo.save(user);

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

    public Optional<UserBean> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

}
