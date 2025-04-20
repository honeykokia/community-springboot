package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.RecordBean;
import com.example.demo.dto.RecordRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.exception.ApiException;
import com.example.demo.service.RecordService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/accounts/{accountId}/records")
public class RecordController {

    @Autowired
    private RecordService recordService;
    
    @GetMapping
    public ResponseEntity<?> getRecordByAccountId(@PathVariable Long accountId) {
        return recordService.getRecordByAccountId(accountId);
    }

    @PostMapping
    public ResponseEntity<?> addRecord(@PathVariable Long accountId , @RequestBody RecordRequest request) {

        ValidationResult<RecordBean> result = recordService.checkRecord(accountId,request);
        if (result.getErrors().isPresent()) {
            throw new ApiException(result.getErrors().get());
        }
        return recordService.saveRecord(accountId,request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRecord(@RequestParam Long accountId) {
        return recordService.recordDelete(accountId);
    }
    

}
