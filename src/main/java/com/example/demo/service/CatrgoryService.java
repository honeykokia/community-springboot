package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.bean.CategoryBean;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.response.SuccessResponse;

@Service
public class CatrgoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    public ResponseEntity<?> getAllCategories(){
        List<CategoryBean> categoryList = categoryRepo.findAll();
        SuccessResponse response = new SuccessResponse(categoryList);
        return ResponseEntity.ok(response);
    }
}
