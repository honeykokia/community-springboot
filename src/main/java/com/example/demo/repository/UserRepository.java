package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.bean.UserBean;

public interface UserRepository extends JpaRepository<UserBean, Long> {

    Optional<UserBean> findByEmail(String email);


}
