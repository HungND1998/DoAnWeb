package com.doan.repository;

import com.doan.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query(value = "FROM Transaction t where t.userWalletId = ?1 and t.chapterId = ?2 and t.status='SUCCESS'")
    Optional<Transaction> findTransaction(Integer userWalletId, Integer chapterId);
    @Query(value = "FROM Transaction  t where t.userWalletId =?1 and t.authorWalletId =?2 and t.mainWalletId=?3 and t.chapterId=?4 and t.status='SUCCESS'")
    Optional<Transaction> findByTransactionExist(Integer userWalletId, Integer authorWalletId, Integer mainWalletId, Integer chapterId);
    @Query(value = "FROM Transaction  t where t.type='PAYMENT' and t.status='SUCCESS'")
    List<Transaction> findAllByPayment();
    @Query(value = "FROM Transaction  t where t.type='TOPUP' and t.status='SUCCESS'")
    List<Transaction> findAllByTopup();
}
