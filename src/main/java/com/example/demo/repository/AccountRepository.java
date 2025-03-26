package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.bean.AccountBean;

public interface AccountRepository extends JpaRepository<AccountBean, Long> {

    Optional<AccountBean> findByEmail(String email);

}
