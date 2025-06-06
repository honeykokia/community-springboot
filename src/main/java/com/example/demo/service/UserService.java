package com.example.demo.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.AccountBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.ErrorResult;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.MemberResponse;
import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.dto.VerifyCodeRequest;
import com.example.demo.enums.AccountStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.EmailJwtUtil;
import com.example.demo.utils.FileUpoladUtil;
import com.example.demo.utils.HashUtil;
import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.PasswordVaildator;
import com.example.demo.utils.ValidationResult;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailJwtUtil emailJwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    public ErrorResult loginCheck(LoginRequest request) {

        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());
        ValidationResult validator = new ValidationResult();

        if (userOpt.isEmpty()) {
            return validator.failFast("email", "帳號或密碼錯誤");
        }

        UserBean user = userOpt.get();

        if (user.getAccountStatus().equals(AccountStatus.UNVERIFIED)) {
            return validator.failFast("email", "帳號尚未驗證，請至信箱收取驗證信");
        }

        validator.checkIsBlank("email", request.getEmail(), "請輸入信箱");
        validator.checkIsBlank("password", request.getPassword(), "請輸入密碼");
        validator.compareinputAndDbPassword("password", user.getPassword(), request.getPassword(), "帳號或密碼錯誤");

        return validator.getErrors();
    }

    public ErrorResult registerCheck(RegisterRequest request) {

        ValidationResult validator = new ValidationResult();
        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            validator.getErrors().add("email", "此信箱已被註冊");
        }

        validator.checkIsBlank("name", request.getName(), "請輸入姓名");
        validator.checkNull("birthday", request.getBirthday(), "請輸入生日");
        validator.checkIsBlank("gender", request.getGender(), "請輸入性別");
        validator.checkIsBlank("email", request.getEmail(), "請輸入信箱");
        validator.checkIsBlank("password", request.getPassword(), "請輸入密碼");
        validator.checkIsBlank("confirmPassword", request.getConfirmPassword(), "請輸入確認密碼");
        validator.comparePassword("confirmPassword", request.getPassword(), request.getConfirmPassword(), "確認密碼不一致");
        validator.passwordVaildator("password", request.getPassword(), "密碼格式不符合，必須要有大小寫英文及數字，且長度要大於8小於12");
        validator.checkMailformat("email", request.getEmail(), "信箱格式不符合");
        validator.checkBirthdayFormat("birthday", request.getBirthday(), "生日不可大於今天");

        return validator.getErrors();
    }

    public ErrorResult updatePasswordCheck(Long userId, PasswordRequest request) {
        ValidationResult validator = new ValidationResult();
        Optional<UserBean> userOpt = userRepo.findById(userId);

        if (userOpt.isEmpty()) {
            validator.getErrors().add("email", "更新異常，請重新登入");
            return validator.getErrors();
        }

        UserBean user = userOpt.get();
        validator.checkIsBlank("oldPassword", request.getOldPassword(), "請輸入舊密碼");
        validator.checkIsBlank("password", request.getPassword(), "請輸入新密碼");
        validator.checkIsBlank("confirmPassword", request.getConfirmPassword(), "請輸入確認密碼");
        validator.compareinputAndDbPassword("oldPassword", request.getOldPassword(), user.getPassword(), "舊密碼錯誤");
        validator.comparePassword("confirmPassword", request.getPassword(), request.getConfirmPassword(), "確認密碼不一致");
        validator.passwordVaildator("password", request.getPassword(), "密碼格式不符合，必須要有大小寫英文及數字，且長度要大於8小於12");

        return validator.getErrors();

    }

    public ResponseEntity<?> loginCreateToken(String email) {

        Optional<UserBean> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ApiException(Map.of("email", "更新異常，請重新登入"));
        }
        UserBean user = userOpt.get();

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        SuccessResponse response = new SuccessResponse(Map.of(
                "token", token,
                "image", user.getImage(),
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "created_at", user.getCreatedAt()));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> registerSave(RegisterRequest request) {
        UserBean user = new UserBean();
        AccountBean account = new AccountBean();
        account.setName("我的現金");
        account.setType((byte) 1);
        account.setDescription("我的現金帳戶");
        account.setImage("/uploads/defaultAccount.jpg");
        account.setInitialAmount(0L);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setIsPublic(false);
        account.setCreatedAt(java.time.LocalDateTime.now());

        user.setName(request.getName());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setPassword(HashUtil.encode(request.getPassword()));
        user.setImage("/uploads/defaultAvatar.jpg");
        user.setRole((byte) 1);
        user.setAccountStatus(AccountStatus.UNVERIFIED);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setAccounts(new ArrayList<>(List.of(account)));
        account.setUser(user);

        userRepo.save(user);

        String token = emailJwtUtil.generateToken(user.getEmail(), user.getId());
        String verifyUrl = baseUrl + "/user/verify?token=" + token;
        emailService.sendEmailByRegister(user.getEmail(), verifyUrl);

        SuccessResponse response = new SuccessResponse(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "created_at", user.getCreatedAt()));

        return ResponseEntity.ok(response);
    }

    public ErrorResult verifyEmail(String token) {

        ValidationResult validator = new ValidationResult();
        String email = emailJwtUtil.getEmailFromToken(token);
        Optional<UserBean> optUser = userRepo.findByEmail(email);

        if (emailJwtUtil.isTokenExpired(token)) {
            return validator.failFast("email", "驗證連結已過期，請重新接收驗證信❌❌");
        }

        if (optUser.isEmpty()) {
            return validator.failFast("email", "查無此會員");
        }
        UserBean user = optUser.get();

        if (user.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            return validator.failFast("email", "此信箱已驗證過👋👋");
        }

        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepo.save(user);

        return validator.getErrors();
    }

    public ResponseEntity<?> resendMail(String email) {

        Optional<UserBean> optUser = userRepo.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("email", "查無此會員"));
        }

        if (optUser.get().getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new ApiException(Map.of("email", "此信箱已驗證過👋👋"));
        }

        String key = "verify:cooldown:" + email;
        if (redisTemplate.hasKey(key)) {
            throw new ApiException(Map.of("email", "請稍後再試，驗證信已發送過"));
        }

        UserBean user = optUser.get();

        String newtoken = emailJwtUtil.generateToken(user.getEmail(), user.getId());
        String verifyUrl = baseUrl + "/user/verify?token=" + newtoken;
        emailService.sendEmailByRegister(user.getEmail(), verifyUrl);
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(3));

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

    @Cacheable(value = "memberProfile", key = "#userId")
    public MemberResponse getMemberProfile(Long userId) {

        System.out.println("📦 查詢資料庫：userId = " + userId);
        Optional<UserBean> optUser = userRepo.findById(userId);
        UserBean userBean = new UserBean();
        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("general", "查無此會員"));
        }

        userBean = optUser.get();
        if (userBean.getGender().equals("male")) {
            userBean.setGender("男");
        } else if (userBean.getGender().equals("female")) {
            userBean.setGender("女");
        } else {
            userBean.setGender("其他");
        }

        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setName(userBean.getName());
        memberResponse.setEmail(userBean.getEmail());
        memberResponse.setBirthday(userBean.getBirthday());
        memberResponse.setGender(userBean.getGender());
        memberResponse.setImage(userBean.getImage());
        
        return memberResponse;
    }

    @CacheEvict(value = "memberProfile", key = "#userId")
    public ResponseEntity<?> updateMemberProfile(Long userId , MemberRequest request, MultipartFile file) {

        Optional<UserBean> optUser = userRepo.findById(userId);
        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("email", "更新異常，請重新登入"));
        }

        UserBean userBean = new UserBean();
        userBean = optUser.get();
        userBean.setName(request.getName());
        userBean.setBirthday(request.getBirthday());
        userBean.setPassword(request.getPassword());

        String image = FileUpoladUtil.uploadFile(file);
        if (!image.equals("")) {
            userBean.setImage(image);
        }

        userRepo.save(userBean);

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updatePassword(Long userId, String password) {
        Optional<UserBean> optUser = userRepo.findById(userId);
        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("email", "更新異常，請重新登入"));
        }
        UserBean user = optUser.get();
        user.setPassword(password);
        userRepo.save(user);

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> forgetPassword(String email) {
        Optional<UserBean> optUser = userRepo.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("email", "請確認信箱是否正確"));
        }

        UserBean user = optUser.get();
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        String key = "forget:password:" + email;
        String verifyToken = emailJwtUtil.generateToken(user.getEmail(), user.getId());

        if (redisTemplate.hasKey(key)) {
            throw new ApiException(Map.of("email", "請稍後再試，驗證信已發送過"));
        }

        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        emailService.sendEmailByForgetPassword(email, code);

        SuccessResponse response = new SuccessResponse(Map.of("verifyToken", verifyToken));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> verifyCode(VerifyCodeRequest request) {

        String email = emailJwtUtil.getEmailFromToken(request.getVerifyToken());
        Optional<UserBean> optUser = userRepo.findByEmail(email);

        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("token", "驗證失敗，請重新操作"));
        }
        UserBean user = optUser.get();

        String key = "forget:password:" + email;
        String correctCode = redisTemplate.opsForValue().get(key);

        if (correctCode == null) {
            throw new ApiException(Map.of("code", "驗證碼已過期，請重新接收驗證信"));
        }

        if (!correctCode.equals(request.getCode())) {
            throw new ApiException(Map.of("code", "驗證碼錯誤，請重新輸入"));
        }

        redisTemplate.delete(key);
        String resetToken = emailJwtUtil.generateToken(user.getEmail(), user.getId());

        SuccessResponse response = new SuccessResponse(Map.of("resetToken", resetToken));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {
        String email = emailJwtUtil.getEmailFromToken(request.getResetToken());
        Optional<UserBean> optUser = userRepo.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new ApiException(Map.of("email", "更新異常，請重新登入"));
        }
        UserBean user = optUser.get();

        if (emailJwtUtil.isTokenExpired(request.getResetToken())) {
            throw new ApiException(Map.of("email", "驗證已過期，請重新接收驗證碼"));
        }

        if (!PasswordVaildator.isValidPassword(request.getPassword())) {
            throw new ApiException(Map.of("password", "密碼格式不符合，必須要有大小寫英文及數字，且長度要大於8小於12"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(Map.of("confirmPassword", "確認密碼不一致"));
        }

        user.setPassword(request.getPassword());
        userRepo.save(user);

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }
}
