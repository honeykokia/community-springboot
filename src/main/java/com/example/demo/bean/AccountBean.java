package com.example.demo.bean;

import java.time.LocalDateTime;

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
@Table(name = "account")
public class AccountBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;
    
    @ManyToOne
    @JoinColumn(name = "style_id")
    private StyleBean style;  

    @Column(name = "item_title")
    private String item_title;

    @Column(name = "item_description")
    private String item_description;

    @Column(name = "item_price")
    private String item_price;

    @Column(name = "item_type")
    private String item_type;

    @Column(name = "item_image")
    private String item_image;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    
}
