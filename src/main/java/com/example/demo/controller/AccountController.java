package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.AccountBean;
import com.example.demo.dto.AccountRequest;
import com.example.demo.dto.ValidationResultOld;
import com.example.demo.exception.ApiException;
import com.example.demo.service.AccountService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<?> getAllAccount() {
        return accountService.getAllAccount();
    }
    
    @PostMapping
    public ResponseEntity<?> addAccount(@RequestBody AccountRequest request) {
        ValidationResultOld<AccountBean> result = accountService.checkAccount(request);

        if(result.getErrors().isPresent()){
            throw new ApiException(result.getErrors().get());
        }

        return accountService.addAccount(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable("id") Long accountId) {

        return accountService.getAccountById(accountId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccountById(@PathVariable Long id, @RequestPart("data") AccountRequest request, @RequestPart(value = "file" , required = false) MultipartFile file) {

        return accountService.updateAccountById(id, request, file);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccountById(@PathVariable Long id) {
        return accountService.deleteAccountById(id);
    }
    
}
