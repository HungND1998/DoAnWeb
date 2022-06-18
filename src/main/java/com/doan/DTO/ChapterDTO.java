package com.doan.DTO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChapterDTO {
    private int id;
    private String content;
    private String name;
    private int price;
    private int storyId;
    private int userId;
    private boolean paymentStatus;
    private String time;
    private boolean status;
}
