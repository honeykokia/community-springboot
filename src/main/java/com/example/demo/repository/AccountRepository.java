package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.bean.AccountBean;

@Repository
public interface AccountRepository extends JpaRepository<AccountBean, Long> {

    List<AccountBean> findAllByUserIdAndStatus(Long userId , byte status);
    Optional<AccountBean> findByIdAndUserIdAndStatus(Long id, Long userId , byte status);

}


