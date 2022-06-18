package com.doan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CHAPTER")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CHAPTER_NAME")
    private String chapterName;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "CREATED_AT")
    private LocalDateTime createAt;

    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

}
