package com.doan.repository;

import com.doan.entity.PaymentConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentConfigRepository  extends JpaRepository<PaymentConfig, Integer> {
    @Query(value = "FROM PaymentConfig p where p.authorId = ?1")
    Optional<PaymentConfig> findByAuthorId(Integer authorId);
}
