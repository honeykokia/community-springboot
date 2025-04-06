package com.example.demo.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name="account")
public class AccountBean {
    
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;

    @OneToMany(mappedBy ="account")
    private List<userAccountFollowBean> followers = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<RecordBean> records = new ArrayList<>();

    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private byte type;
    @Column(name = "description")
    private String description;
    @Column(name = "image")
    private String image;
    @Column(name = "initial_amount")
    private Long initial_amount;
    @Column(name = "current_amount")
    private boolean is_public;
    @Column(name = "is_public")
    private LocalDateTime created_at;

}
