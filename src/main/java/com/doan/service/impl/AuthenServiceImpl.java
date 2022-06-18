package com.doan.service.impl;

import com.doan.DTO.RegisterDTO;
import com.doan.entity.Role;
import com.doan.entity.User;
import com.doan.entity.Wallet;
import com.doan.repository.RoleRepository;
import com.doan.repository.UserRepository;
import com.doan.repository.WalletRepository;
import com.doan.service.AuthenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AuthenServiceImpl implements AuthenService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public boolean register(RegisterDTO register) {
        boolean check = false;
        User user1 = userRepository.findByUsername(register.getUsername()).orElse(null);
        if(Objects.nonNull(user1)) {
            check = false;
        }
        List<Role> roles;
        if(register.isRoleAuthor()) {
            String[] role = {"ROLE_USER","ROLE_AUTHOR"};
            roles = roleRepository.findAllByRoleIn(role);
        } else {
            String[] role = {"ROLE_USER"};
            roles = roleRepository.findAllByRoleIn(role);
        }
        if(Objects.nonNull(register)) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = new User();
            user.setPassword(passwordEncoder.encode(register.getPassword()));
            user.setRoles(roles);
            user.setCreatedAt(LocalDateTime.now());
            user.setStatus(true);
            user.setUsername(register.getUsername());
            user.setName(register.getName());
            user.setPasswordRegister(register.getPassword());
            user.setPasswordRegisterConfirm(register.getPasswordConfirm());
            userRepository.save(user);

            User user2 = userRepository.findByUsername(user.getUsername()).orElse(null);
            if(Objects.nonNull(user2)) {
                Wallet wallet = new Wallet();
                wallet.setUser(user2);
                wallet.setCreatedAt(LocalDateTime.now());
                wallet.setAmount(0);
                wallet.setMainWallet(false);
                walletRepository.save(wallet);
            }
            check = true;
        }
        return check;
    }
}
