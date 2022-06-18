package com.doan.entity;

import com.doan.utils.MessageError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "WALLET")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "main_wallet")
    private boolean mainWallet;
}
