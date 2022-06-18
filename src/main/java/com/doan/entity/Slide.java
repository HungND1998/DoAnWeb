package com.doan.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SLIDE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Slide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "story_id")
    private Integer storyId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

}
