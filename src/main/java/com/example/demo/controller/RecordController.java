package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.RecordBean;
import com.example.demo.dto.ErrorResult;
import com.example.demo.dto.RecordRequest;
import com.example.demo.dto.ValidationResultOld;
import com.example.demo.exception.ApiException;
import com.example.demo.service.RecordService;
import com.example.demo.utils.AuthUtil;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/account/{accountId}/records")
public class RecordController {

    @Autowired
    private RecordService recordService;
    
    @GetMapping
    public ResponseEntity<?> getRecordByAccountId(
        @PathVariable Long accountId ,
        @RequestParam(defaultValue = "0") int page , 
        @RequestParam(defaultValue = "10") int size ,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate) {
            
        Long userId = AuthUtil.getCurrentUserId();
        return recordService.getRecordByAccountId(userId , accountId, page , size ,startDate, endDate);
    }

    @PostMapping
    public ResponseEntity<?> saveRecord(@PathVariable Long accountId , @RequestBody RecordRequest request) {
        Long userId = AuthUtil.getCurrentUserId();
        ErrorResult result = recordService.checkRecord(userId,accountId,request);
        if (result.hasErrors()) {
            throw new ApiException(result.getErrors());
        }
        return recordService.saveRecord(userId,accountId,request);
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateRecord(@PathVariable Long accountId, @PathVariable Long recordId , @RequestBody RecordRequest request) {
        Long userId = AuthUtil.getCurrentUserId();
        return recordService.updateRecord(userId,accountId,recordId,request);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long accountId , @PathVariable Long recordId) {
        Long userId = AuthUtil.getCurrentUserId();
        return recordService.deleteRecord(userId,accountId,recordId);
    }
    

}
