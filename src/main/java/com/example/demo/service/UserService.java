package com.example.demo.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.AccountBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.MemberRequest;
import com.example.demo.dto.PasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.enums.AccountStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.AuthUtil;
import com.example.demo.utils.EmailJwtUtil;
import com.example.demo.utils.EmailVaildator;
import com.example.demo.utils.FileUpoladUtil;
import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.ValidationUtils;



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

    public ValidationResult<UserBean> loginCheck(LoginRequest request) {

        ValidationResult<UserBean> result = new ValidationResult<UserBean>();
        HashMap<String,String> errors = new HashMap<String,String>();
        Optional<UserBean> userOpt = userRepo.findByEmail(request.getEmail());


        if(userOpt.isEmpty()) {
            return ValidationResult.failFast("email", "帳號或密碼錯誤");
        }

        UserBean user = userOpt.get();

        if(user.getAccountStatus().equals(AccountStatus.UNVERIFIED)){
            return ValidationResult.failFast("verify", "此信箱尚未驗證，請至信箱收取驗證信");
        }

        ValidationUtils.checkIsBlank(errors, "email", request.getEmail(), "請輸入信箱");
        ValidationUtils.checkIsBlank(errors, "password", request.getPassword(), "請輸入密碼");
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

    public ValidationResult<UserBean> updatePasswordCheck(PasswordRequest request){

        Long userId = AuthUtil.getCurrentUserId();

        ValidationResult<UserBean> result = new ValidationResult<UserBean>();
        HashMap<String,String> errors = new HashMap<String,String>();
        Optional<UserBean> userOpt = userRepo.findById(userId);

        if(userOpt.isEmpty()) {
            return ValidationResult.failFast("oldPassword", "查無此會員");
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

    public ResponseEntity<?> loginCreateToken(UserBean user) {

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        SuccessResponse response = new SuccessResponse(Map.of(
                "token", token,
                "image", user.getImage(),
                "id", user.getId(),
                "email", user.getEmail(),
                "name" , user.getName(),
                "created_at", user.getCreated_at()));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> registerSave(RegisterRequest request) {
        UserBean user = new UserBean();
        AccountBean account = new AccountBean();
        account.setName("我的現金");
        account.setType((byte) 1);
        account.setDescription("我的現金帳戶");
        account.setImage("/uploads/defaultAccount.jpg");
        account.setInitial_amount(0L);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setIs_public(false);
        account.setCreated_at(java.time.LocalDateTime.now());
        

        user.setName(request.getName());
        user.setBirthday(request.getBirthday());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setImage("/uploads/defaultAvatar.jpg");
        user.setRole((byte) 1);
        user.setAccountStatus(AccountStatus.UNVERIFIED);
        user.setCreated_at(java.time.LocalDateTime.now());
        user.setAccounts(new ArrayList<>(List.of(account)));
        account.setUser(user);

        userRepo.save(user);

        String token = emailJwtUtil.generateToken(user.getEmail(), user.getId());
        String verifyUrl= baseUrl + "/user/verify?token=" + token;
        emailService.sendEmail(user.getEmail(), verifyUrl);

        SuccessResponse response = new SuccessResponse(Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "created_at", user.getCreated_at()));

        return ResponseEntity.ok(response);
    }

    public ValidationResult<UserBean> verifyEmail(String token) {

        ValidationResult<UserBean> result = new ValidationResult<UserBean>();
        HashMap<String,String> errors = new HashMap<String,String>();

        String email = emailJwtUtil.getEmailFromToken(token);
        Optional<UserBean> optUser = userRepo.findByEmail(email);

        if(emailJwtUtil.isTokenExpired(token)){
           return ValidationResult.failFast("email", "驗證連結已過期，請重新接收驗證信❌❌");
        }

        if(optUser.isEmpty()){
            return ValidationResult.failFast("email", "查無此會員");
        }
        UserBean user = optUser.get();

        if(user.getAccountStatus().equals(AccountStatus.ACTIVE)){
            return ValidationResult.failFast("email", "此信箱已驗證過👋👋");
        }
        
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepo.save(user);

        if(errors.isEmpty()){
            result.setErrors(Optional.empty());
        }else{
            result.setErrors(Optional.of(errors));
        }
        result.setTarget(user);

        return result;
    }

    public ResponseEntity<?> resendMail(String email) {

        Optional<UserBean> optUser = userRepo.findByEmail(email);
        if(optUser.isEmpty()){
            throw new ApiException(Map.of("email", "查無此會員"));
        }

        if(optUser.get().getAccountStatus().equals(AccountStatus.ACTIVE)){
            throw new ApiException(Map.of("email", "此信箱已驗證過👋👋"));
        }

        String key = "verify:cooldown:" + email;
        if(redisTemplate.hasKey(key)){
            throw new ApiException(Map.of("email", "請稍後再試，驗證信已發送過"));
        }

        UserBean user = optUser.get();

        String newtoken = emailJwtUtil.generateToken(user.getEmail(), user.getId());
        String verifyUrl= baseUrl + "/user/verify?token=" + newtoken;
        emailService.sendEmail(user.getEmail(), verifyUrl);
        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(3));

        SuccessResponse response = new SuccessResponse(null);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getMemberProfile() {

        Long userId = AuthUtil.getCurrentUserId();
        if (userId == null){
            throw new ApiException(Map.of("general", "查無此會員"));
        }

        Optional<UserBean> optUser = userRepo.findById(userId);
        UserBean userBean = new UserBean();
        if(optUser.isEmpty()){
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

        SuccessResponse response = new SuccessResponse(userBean);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateMemberProfile(MemberRequest request, MultipartFile file) {
        Long userId = AuthUtil.getCurrentUserId();
        Optional<UserBean> optUser = userRepo.findById(userId);
        if(optUser.isEmpty()){
            throw new ApiException(Map.of("general", "帳號更新失敗"));
        }
        UserBean userBean = new UserBean();
        userBean = optUser.get();
        userBean.setName(request.getName());
        userBean.setBirthday(request.getBirthday());
        userBean.setPassword(request.getPassword());

        String image = FileUpoladUtil.uploadFile(file);
        if (!image.equals("")){
            userBean.setImage(image);
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

}
