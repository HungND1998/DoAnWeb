package com.doan.entity;

import com.doan.utils.MessageError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "password_register")
    private String passwordRegister;

    @Column(name = "password_register_confirm")
    private String passwordRegisterConfirm;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "status")
    private boolean status;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))

    private List< Role > roles;

    @OneToMany(mappedBy = "user")
    List<Story> stories;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;

}
