package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CatrgoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CatrgoryService categoryService;

    @PostMapping("/list")
    public ResponseEntity<?> getCategory() {
        return categoryService.categoryGet();
    }
}
