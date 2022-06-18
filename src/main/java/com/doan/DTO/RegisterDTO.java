package com.doan.DTO;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Size;

@Data
@ToString
public class RegisterDTO {
    private String username;
    @Size(min = 8, message = "Yêu cầu nhập mật khẩu 8 kí tự")
    private String password;
    private String passwordConfirm;
    private String name;
    private boolean roleAuthor;
}
