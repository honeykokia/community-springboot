package com.example.demo.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.bean.AccountBean;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.response.SuccessResponse;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    public ResponseEntity<?> getAccount(String email) {

        Optional<AccountBean> accountOpt = accountRepo.findByEmail(email);

        if (accountOpt.isEmpty()) {
            throw new ApiException(Map.of("general", "連線異常"));
        }
        SuccessResponse response = new SuccessResponse(accountOpt.get());

        return ResponseEntity.ok(response);
    }
    
}
