package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.bean.RecordBean;


@Repository
public interface RecordRepository extends JpaRepository<RecordBean, Long> {

    @Query("SELECT a FROM RecordBean a WHERE a.user.id = :id")
    Optional<List<RecordBean>> findByUserId(@Param("id") Long userId);

    Optional<List<RecordBean>> findByAccountId(Long accountId);
    
    Page<RecordBean> findByAccountId(Long accountId, Pageable pageable);

    Page<RecordBean> findByAccountIdAndItemDateBetween(Long accountId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT SUM(r.itemPrice) FROM RecordBean r WHERE r.account.id = :accountId AND r.category.type = 1")
    Long sumIncome(@Param("accountId") Long accountId);

    @Query("SELECT SUM(r.itemPrice) FROM RecordBean r WHERE r.account.id = :accountId AND r.category.type = -1")
    Long sumExpense(@Param("accountId") Long accountId);

    @Query("SELECT SUM(r.itemPrice) FROM RecordBean r WHERE r.account.id = :accountId AND r.category.type = 1 AND r.itemDate BETWEEN :start AND :end")
    Long sumIncomeBetween(@Param("accountId") Long accountId, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT SUM(r.itemPrice) FROM RecordBean r WHERE r.account.id = :accountId AND r.category.type = -1 AND r.itemDate BETWEEN :start AND :end")
    Long sumExpenseBetween(@Param("accountId") Long accountId, @Param("start") LocalDate start, @Param("end") LocalDate end);

}
