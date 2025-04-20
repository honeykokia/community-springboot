package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.bean.AccountBean;
import com.example.demo.bean.CategoryBean;
import com.example.demo.bean.RecordBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.RecordRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.enums.AccountStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.AuthUtil;
import com.example.demo.utils.ValidationUtils;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    public ValidationResult<RecordBean> checkRecord(Long accountId , RecordRequest request) {
        Long userId = AuthUtil.getCurrentUserId();
        ValidationResult<RecordBean> result = new ValidationResult<>();
        Map<String, String> errors = new HashMap<>();

        Optional<UserBean> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return ValidationResult.failFast("user", "使用者不存在");
        }

        ValidationUtils.checkNull(errors, "account_id", accountId, "請選擇帳戶");

        ValidationUtils.checkNull(errors,"category_id",request.getCategory_id(), "請選擇類別");

        ValidationUtils.checkNull(errors, "item_date", request.getItem_date(), "請選擇日期");

        ValidationUtils.checkNull(errors, "item_price", request.getItem_price(), "請輸入金額");

        
        ValidationUtils.checkIsNegative(errors, "item_price", request.getItem_price(), "金額不能小於0");

        if (errors.isEmpty()) {
            result.setErrors(Optional.empty());
        }else{
            result.setErrors(Optional.of(errors));
        }
        result.setTarget(null);

        return result;

    }


    public ResponseEntity<?> getRecordByAccountId(Long accountId) {

        Long userId = AuthUtil.getCurrentUserId();
        accountRepo.findByIdAndUserIdAndStatus(accountId, userId , AccountStatus.ACTIVE.getCode())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到帳戶")));

        Optional<List<RecordBean>> recordOpt = recordRepo.findByAccountId(accountId);
        
        SuccessResponse response;
        if (recordOpt.isEmpty()) {
            response = new SuccessResponse(new RecordBean());
        }else{
            response = new SuccessResponse(recordOpt.get());
        }


        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> saveRecord(Long accountId , RecordRequest request){
        Long userId = AuthUtil.getCurrentUserId();
        UserBean user = userRepo.findById(userId)
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到使用者")));

        AccountBean account = accountRepo.findByIdAndUserIdAndStatus(accountId, userId , AccountStatus.ACTIVE.getCode())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到帳戶")));

        CategoryBean category = categoryRepo.findById(request.getCategory_id())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到類別")));

        RecordBean record = new RecordBean();

        if(category.getType() == -1){
            account.setInitial_amount(account.getInitial_amount() - request.getItem_price());
        }else if(category.getType() == 1){
            account.setInitial_amount(account.getInitial_amount() + request.getItem_price());
        }

        accountRepo.save(account);

        account.addRecord(record);
        user.addRecord(record);

        record.setCategory(category);
        record.setItem_price(request.getItem_price());
        record.setItem_note(request.getItem_note());
        record.setItem_date(request.getItem_date());
        record.setCreated_at(LocalDateTime.now());
        record.setUpdated_at(LocalDateTime.now());
        recordRepo.save(record);

        return ResponseEntity.ok(new SuccessResponse(null));
    }

    public ResponseEntity<?> recordDelete(long recordId){

        Optional<RecordBean> accountOpt = recordRepo.findById(recordId);
        if(accountOpt.isEmpty()){
            throw new ApiException(Map.of("general","刪除失敗"));
        }
        RecordBean record = accountOpt.get();
        recordRepo.delete(record);
        return ResponseEntity.ok(new SuccessResponse(null));
    }
    
}
