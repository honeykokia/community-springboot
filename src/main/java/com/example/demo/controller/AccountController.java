package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AccountRequest;
import com.example.demo.service.AccountService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    
    @PostMapping("/list")
    public ResponseEntity<?> account() {
        return accountService.accountGet();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAccount(@RequestBody AccountRequest request) {

        return accountService.accountSave(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam Long accountId) {
        return accountService.accountDelete(accountId);
    }
    

}
