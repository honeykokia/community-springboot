package com.example.demo.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;

    @JsonIgnore
    @OneToMany(mappedBy ="account")
    private List<userAccountFollowBean> followers = new ArrayList<>();

    @JsonIgnore
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
    private Long initialAmount;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "status")
    private byte status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AccountStatus getAccountStatus(){
        return AccountStatus.fromCode(status);
    }

    public void setAccountStatus(AccountStatus status){
        this.status = status.getCode();
    }

    public void addRecord(RecordBean record) {
        records.add(record);
        record.setAccount(this);
    }

}
