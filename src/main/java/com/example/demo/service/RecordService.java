package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.bean.AccountBean;
import com.example.demo.bean.CategoryBean;
import com.example.demo.bean.RecordBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.ErrorResult;
import com.example.demo.dto.PageRecordResponse;
import com.example.demo.dto.RecordRequest;

import com.example.demo.enums.AccountStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;

import com.example.demo.utils.ValidationResult;


import lombok.val;

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

    public ErrorResult checkRecord(Long userId , Long accountId , RecordRequest request) {

        ValidationResult validator = new ValidationResult();

        Optional<UserBean> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return validator.failFast("user", "使用者不存在");
        }

        validator.checkNull("account_id", accountId, "請選擇帳戶");
        validator.checkNull("category_id", request.getCategoryId(), "請選擇類別");
        validator.checkIsBlank("itemNote", request.getItemNote(), "請輸入備註");
        validator.checkNull("itemPrice", request.getItemPrice(), "請輸入金額");
        validator.checkIsNegative("itemPrice", request.getItemPrice(), "金額不能小於0");

        return validator.getErrors();

    }


    public ResponseEntity<?> getRecordByAccountId(Long userId , Long accountId, int page , int size , LocalDate startDate , LocalDate endDate) {

        
        accountRepo.findByIdAndUserIdAndStatus(accountId, userId , AccountStatus.ACTIVE.getCode())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到帳戶")));

        Pageable pageable = PageRequest.of(page, size,Sort.by("itemDate").descending());
        Page<RecordBean> recordPage = null;
        Long income;
        Long expense;
        if (startDate != null && endDate != null) {
            recordPage = recordRepo.findByAccountIdAndItemDateBetween(accountId, startDate, endDate, pageable);
            // income = recordRepo.sumIncomeBetween(accountId, startDate, endDate);
            // expense = recordRepo.sumExpenseBetween(accountId, startDate, endDate);
        } else {
            recordPage = recordRepo.findByAccountId(accountId, pageable);

        }
        income = recordRepo.sumIncome(accountId);
        expense = recordRepo.sumExpense(accountId);
        
        SuccessResponse response = new SuccessResponse(new PageRecordResponse(recordPage,income,expense));

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> saveRecord(Long userId , Long accountId , RecordRequest request){
        
        UserBean user = userRepo.findById(userId)
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到使用者")));

        AccountBean account = accountRepo.findByIdAndUserIdAndStatus(accountId, userId , AccountStatus.ACTIVE.getCode())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到帳戶")));

        CategoryBean category = categoryRepo.findById(request.getCategoryId())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到類別")));

        RecordBean record = new RecordBean();

        accountRepo.save(account);

        account.addRecord(record);
        user.addRecord(record);

        record.setCategory(category);
        record.setItemPrice(request.getItemPrice());
        record.setItemNote(request.getItemNote());
        record.setItemDate(request.getItemDate());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        recordRepo.save(record);

        return ResponseEntity.ok(new SuccessResponse(null));
    }


    public ResponseEntity<?> updateRecord(Long userId , Long accountId , Long recordId , RecordRequest request){
        
        Optional<RecordBean> recordOpt = recordRepo.findById(recordId);
        if(recordOpt.isEmpty()){
            throw new ApiException(Map.of("general","更新失敗"));
        }

        Optional<AccountBean> accountOpt = accountRepo.findByIdAndUserIdAndStatus(accountId, userId, AccountStatus.ACTIVE.getCode());
        if(accountOpt.isEmpty()){
            throw new ApiException(Map.of("general","找不到帳戶"));
        }

        AccountBean account = accountOpt.get();
        RecordBean record = recordOpt.get();

        if(!record.getAccount().getId().equals(account.getId())){
            throw new ApiException(Map.of("general","帳戶不正確"));
        }
        
        CategoryBean category = categoryRepo.findById(request.getCategoryId())
            .orElseThrow(() -> new ApiException(Map.of("general", "找不到類別")));

        record.setAccount(account);
        record.setCategory(category);
        record.setItemPrice(request.getItemPrice());
        record.setItemNote(request.getItemNote());
        record.setItemDate(request.getItemDate());
        record.setUpdatedAt(LocalDateTime.now());
        
        recordRepo.save(record);

        return ResponseEntity.ok(new SuccessResponse(null));
    }

    public ResponseEntity<?> deleteRecord(Long userId,Long accountId , Long recordId){
        
        Optional<RecordBean> recordOpt = recordRepo.findById(recordId);
        if(recordOpt.isEmpty()){
            throw new ApiException(Map.of("general","刪除失敗"));
        }

        Optional<AccountBean> accountOpt = accountRepo.findByIdAndUserIdAndStatus(accountId, userId, AccountStatus.ACTIVE.getCode());
        if(accountOpt.isEmpty()){
            throw new ApiException(Map.of("general","找不到帳戶"));
        }
        AccountBean account = accountOpt.get();
        RecordBean record = recordOpt.get();

        if(!record.getAccount().getId().equals(account.getId())){
            throw new ApiException(Map.of("general","帳戶不正確"));
        }
        recordRepo.delete(record);
        return ResponseEntity.ok(new SuccessResponse(null));
    }
    
}
