package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.RecordBean;
import com.example.demo.dto.RecordRequest;
import com.example.demo.dto.ValidationResult;
import com.example.demo.exception.ApiException;
import com.example.demo.service.RecordService;

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
    
        return recordService.getRecordByAccountId(accountId, page , size ,startDate, endDate);
    }

    @PostMapping
    public ResponseEntity<?> addRecord(@PathVariable Long accountId , @RequestBody RecordRequest request) {

        ValidationResult<RecordBean> result = recordService.checkRecord(accountId,request);
        if (result.getErrors().isPresent()) {
            throw new ApiException(result.getErrors().get());
        }
        return recordService.saveRecord(accountId,request);
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateRecord(@PathVariable Long accountId, @PathVariable Long recordId , @RequestBody RecordRequest request) {
        
        return recordService.updateRecord(accountId,recordId,request);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long accountId , @PathVariable Long recordId) {
        return recordService.deleteRecord(accountId,recordId);
    }
    

}
