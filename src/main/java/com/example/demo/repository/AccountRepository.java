package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.bean.AccountBean;



public interface AccountRepository extends JpaRepository<AccountBean, Long> {

    @Query("SELECT a FROM AccountBean a WHERE a.user.id = :id")
    Optional<List<AccountBean>> findByUserId(@Param("id") Long userId);

}
