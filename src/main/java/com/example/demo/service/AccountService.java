package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.AccountBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.AccountRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.enums.AccountStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.AuthUtil;
import com.example.demo.utils.FileUpoladUtil;
import com.example.demo.utils.ValidationUtils;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private UserRepository userRepo;

    public ValidationResult<AccountBean> checkAccount(AccountRequest request) {
        Long userId = AuthUtil.getCurrentUserId();
        ValidationResult<AccountBean> result = new ValidationResult<>();
        Map<String, String> errors = new HashMap<>();

        Optional<UserBean> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return ValidationResult.failFast("user", "使用者不存在");
        }

        ValidationUtils.checkIsBlank(errors, "name", request.getName(), "請輸入帳號名稱");
        ValidationUtils.checkIsBlank(errors, "description", request.getDescription(), "請輸入帳號描述");
        ValidationUtils.checkNull(errors,"initialAmount", request.getInitialAmount(), "請輸入金額");

        
        UserBean user = userOpt.get();
        List<AccountBean> existingAccounts = user.getAccounts();
        for (AccountBean account : existingAccounts){
            if(account.getName().equals(request.getName()) && account.getAccountStatus() == AccountStatus.ACTIVE){
                errors.put("name", "帳戶名稱已存在");
                break;
            }
        }

        List<AccountBean> accountList = accountRepo.findAllByUserIdAndStatus(userId,AccountStatus.ACTIVE.getCode());

        if(accountList.size() >= 5) {
            errors.put("name", "帳戶數量已達上限");
        }

        ValidationUtils.checkIsNegative(errors, "initialAmount", request.getInitialAmount(), "金額不能小於0");

        if (errors.isEmpty()){
            result.setErrors(Optional.empty());
        }else{
            result.setErrors(Optional.of(errors));
        }
        result.setTarget(null);

        return result;
    }

    public ResponseEntity<SuccessResponse> getAllAccount(){
        Long userId = AuthUtil.getCurrentUserId();
        
        List<AccountBean> accountList = accountRepo.findAllByUserIdAndStatus(userId, AccountStatus.ACTIVE.getCode());
        

        if (accountList.isEmpty()) {
            throw new ApiException(Map.of("account", "帳戶不存在"));
        }
        SuccessResponse response = new SuccessResponse(accountList);

        return ResponseEntity.ok(response);
    }
    public ResponseEntity<SuccessResponse> addAccount(AccountRequest request) {
        Long userId = AuthUtil.getCurrentUserId();
        
        Optional<UserBean> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ApiException(Map.of("user", "使用者不存在"));
        }

        UserBean user = userOpt.get();
        AccountBean account = new AccountBean();
        account.setName(request.getName());
        account.setType((byte)1);
        account.setDescription(request.getDescription());
        account.setImage("/uploads/defaultAccount.jpg");
        account.setInitialAmount(request.getInitialAmount());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        account.setIsPublic(request.getIsPublic());
        user.addAccount(account);

        accountRepo.save(account);

        SuccessResponse response = new SuccessResponse(Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "created_at", user.getCreatedAt()));

        
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<?> getAccountById(Long accountId) {
        Long userId = AuthUtil.getCurrentUserId();
        
        Optional<AccountBean> accountOpt = accountRepo.findByIdAndUserIdAndStatus(accountId, userId, AccountStatus.ACTIVE.getCode());

        
        AccountBean account = accountOpt.get();
        SuccessResponse response = new SuccessResponse(account);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateAccountById(Long accountId, AccountRequest request , MultipartFile file) {
        Long userId = AuthUtil.getCurrentUserId();
        Optional<AccountBean> accountOpt = accountRepo.findByIdAndUserIdAndStatus(accountId, userId, AccountStatus.ACTIVE.getCode());

        if (accountOpt.isEmpty()) {
            throw new ApiException(Map.of("account", "帳戶不存在"));
        }
        
        Optional<UserBean> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ApiException(Map.of("user", "使用者不存在"));
        }

        AccountBean account = accountOpt.get();
        account.setName(request.getName());
        account.setDescription(request.getDescription());
        account.setInitialAmount(request.getInitialAmount());
        account.setIsPublic(request.getIsPublic());

        String image = FileUpoladUtil.uploadFile(file);
        if (!image.equals("")){
            account.setImage(image);
        }

        accountRepo.save(account);

        SuccessResponse response = new SuccessResponse(account);

        return ResponseEntity.ok(response); 
    }

    public ResponseEntity<?> deleteAccountById(Long accountId) {
        Long userId = AuthUtil.getCurrentUserId();
        Optional<AccountBean> accountOpt = accountRepo.findByIdAndUserIdAndStatus(accountId, userId, AccountStatus.ACTIVE.getCode());

        if (accountOpt.isEmpty()) {
            throw new ApiException(Map.of("general", "帳戶不存在"));
        }

        List<AccountBean> accountList = accountRepo.findAllByUserIdAndStatus(userId , AccountStatus.ACTIVE.getCode());

        if (accountList.size() <= 1) {
            throw new ApiException(Map.of("general", "至少要有一個帳戶"));
        }

        AccountBean account = accountOpt.get();
        account.setAccountStatus(AccountStatus.DISABLED);
        // accountRepo.delete(account);
        accountRepo.save(account);

        SuccessResponse response = new SuccessResponse(Map.of("message", "帳戶已刪除"));

        return ResponseEntity.ok(response);
    }
}
