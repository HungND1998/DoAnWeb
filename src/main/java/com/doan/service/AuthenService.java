package com.doan.service;

import com.doan.DTO.RegisterDTO;
import com.doan.entity.User;

public interface AuthenService {
    boolean register(RegisterDTO user);
}
