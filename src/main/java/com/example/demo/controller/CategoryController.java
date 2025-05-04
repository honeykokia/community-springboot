package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CatrgoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CatrgoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
