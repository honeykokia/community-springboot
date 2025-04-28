package com.example.demo.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.bean.RecordBean;

import lombok.Data;

@Data
public class PageRecordResponse {
    private List<RecordBean> content;
    private int totalPages;
    private int currentPages;
    private long totalElements;
    private Long incomeTotal;
    private Long expenseTotal;

    public PageRecordResponse(Page<RecordBean> page , Long income, Long expense) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPages = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.incomeTotal = income;
        this.expenseTotal = expense;
    }

}
