package com.doan.service.impl;

import com.doan.entity.Category;
import com.doan.entity.Role;
import com.doan.entity.Story;
import com.doan.entity.User;
import com.doan.repository.CategoryRepository;
import com.doan.repository.RoleRepository;
import com.doan.repository.StoryRepository;
import com.doan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return Optional.ofNullable(optionalUser).orElseThrow(()->new UsernameNotFoundException("Username Not Found"))
                .map(MyUserDetails::new).get();
    }

}
