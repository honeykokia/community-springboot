package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecordRequest {

    private Long categoryId;
    private Long itemPrice;
    private String itemNote;
    private LocalDate itemDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
