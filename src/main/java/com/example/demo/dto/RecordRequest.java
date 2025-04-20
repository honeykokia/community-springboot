package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecordRequest {

    private Long category_id;
    private Long item_price;
    private String item_note;
    private LocalDate item_date;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
