package com.doan.DTO;

import lombok.Data;

import java.util.List;

@Data
public class StoryDTO {
    private Integer id;
    private String content;
    private String name;
    private String image;
    private int view;
    private boolean isAuthor;
    private boolean isAdmin;
    List<ChapterDTO> chapters;
}
