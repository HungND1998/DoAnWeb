package com.doan.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TopupDTO {
    private int id;
    private String user;
    private int amount;
    private LocalDateTime created_at;
}
