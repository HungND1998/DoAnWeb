package com.doan.repository;

import com.doan.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    @Query(value = "FROM Wallet  w where  w.user.id = ?1")
    Optional<Wallet> findByUserId(Integer userId);

    @Query(value = "FROM Wallet  w where w.mainWallet = true")
    Optional<Wallet> findMainWallet();
}
