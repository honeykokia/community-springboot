package com.example.demo.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.enums.AccountStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountBean> accounts = new ArrayList<>();
    
    @OneToMany(mappedBy = "follower")
    private List<userAccountFollowBean> followingAccount = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<RecordBean> records = new ArrayList<>();

    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "gender")
    private String Gender;
    @Column(name = "birthday")
    private LocalDate birthday;
    @Column(name = "image")
    private String image;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @Column(name = "status")
    private byte status;
    @Column(name = "role")
    private byte role;


    public AccountStatus getAccountStatus(){
        return AccountStatus.fromCode(status);
    }

    public void setAccountStatus(AccountStatus status){
        this.status = status.getCode();
    }

    public void addRecord(RecordBean record){
        this.records.add(record);
        record.setUser(this);
    }
    public void addAccount(AccountBean account){
        this.accounts.add(account);
        account.setUser(this);
    }

    


    
}
