package com.doan.repository;

import com.doan.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findAll();
    List<Role> findAllByRoleIn(String[] role);
}
