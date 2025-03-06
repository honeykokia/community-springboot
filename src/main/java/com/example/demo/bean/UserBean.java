package com.example.demo.bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "Gender")
    private String Gender;
    @Column(name = "birthday")
    private LocalDateTime birthday;
    @Column(name = "image")
    private String image_path;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @Column(name = "last_login_at")
    private LocalDateTime last_login_at;
    @Column(name = "is_active")
    private boolean is_active;
    @Column(name = "role")
    private byte role;
}
