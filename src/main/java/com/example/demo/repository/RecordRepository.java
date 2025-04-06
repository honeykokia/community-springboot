package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.bean.RecordBean;


@Repository
public interface RecordRepository extends JpaRepository<RecordBean, Long> {

    @Query("SELECT a FROM RecordBean a WHERE a.user.id = :id")
    Optional<List<RecordBean>> findByUserId(@Param("id") Long userId);

}
