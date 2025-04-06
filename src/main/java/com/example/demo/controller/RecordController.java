package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RecordRequest;
import com.example.demo.service.RecordService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private RecordService recordService;
    
    @PostMapping("/list")
    public ResponseEntity<?> account() {
        return recordService.recordGet();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAccount(@RequestBody RecordRequest request) {

        return recordService.recordSave(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam Long accountId) {
        return recordService.recordDelete(accountId);
    }
    

}
