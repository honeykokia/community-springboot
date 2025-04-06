package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.bean.UserBean;

@Repository
public interface UserRepository extends JpaRepository<UserBean, Long> {

    Optional<UserBean> findById(Long id);
    Optional<UserBean> findByEmail(String email);

}
