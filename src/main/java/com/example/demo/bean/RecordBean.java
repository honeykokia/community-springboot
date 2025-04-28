package com.example.demo.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "record")
public class RecordBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountBean account; 

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryBean category;

    @Column(name = "item_title")
    private String itemTitle;

    @Column(name = "item_note")
    private String itemNote;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "item_price")
    private Long itemPrice;

    @Column(name = "item_image")
    private String itemImage;

    @Column(name = "item_date")
    private LocalDate itemDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    
}
