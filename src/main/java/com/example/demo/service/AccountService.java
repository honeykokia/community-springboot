package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.bean.AccountBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.AccountRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.AuthUtil;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private UserRepository userRepo;

    public ResponseEntity<?> accountGet() {

        Long userId = AuthUtil.getCurrentUserId();
        Optional<List<AccountBean>> accountOpt = accountRepo.findByUserId(userId);
        
        SuccessResponse response;
        if (accountOpt.isEmpty()) {
            response = new SuccessResponse(new AccountBean());
        }else{
            response = new SuccessResponse(accountOpt.get());
        }


        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> accountSave(AccountRequest request){
        Long userId = AuthUtil.getCurrentUserId();
        UserBean user = userRepo.findById(userId).get();
        AccountBean account = new AccountBean();
        account.setUser(user);
        account.setItem_title(request.getItem_title());
        account.setItem_description(request.getItem_description());
        account.setItem_price(request.getItem_price());
        account.setItem_type(request.getItem_type());
        account.setItem_image(request.getItem_image());
        account.setItem_date(request.getItem_date());
        account.setCreated_at(LocalDateTime.now());
        accountRepo.save(account);

        return ResponseEntity.ok(new SuccessResponse(null));
    }

    public ResponseEntity<?> accountDelete(long accountId){

        Optional<AccountBean> accountOpt = accountRepo.findById(accountId);
        if(accountOpt.isEmpty()){
            throw new ApiException(Map.of("general","刪除失敗"));
        }
        AccountBean account = accountOpt.get();
        accountRepo.delete(account);
        return ResponseEntity.ok(new SuccessResponse(null));
    }
    
}
