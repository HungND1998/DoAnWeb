package com.doan.DTO;

import lombok.Data;

@Data
public class PasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
