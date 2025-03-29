package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AccountRequest {

    private String item_title;
    private String item_description;
    private int item_price;
    private String item_type;
    private String item_image;
    private LocalDate item_date;
    
}
