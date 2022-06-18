package com.doan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_wallet_id", nullable = false)
    private Integer userWalletId;

    @Column(name = "author_wallet_id")
    private Integer authorWalletId;

    @Column(name = "main_wallet_id")
    private Integer mainWalletId;

    @Column(name = "chapter_id")
    private int chapterId;

    @Column(name = "amount")
    private int amount;

    @Column(name = "charge_rate")
    private int chargeRate;

    @Column(name = "type")
    private String type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    private String status;


}
