package com.doan.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "STORY")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "STORY_NAME")
    private String storyName;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;


    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    private boolean status;

    @Column(name = "view")
    private int view;

    @ManyToMany
    @JoinTable(
            name = "CATEGORY_STORY",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "story")
    private List<Chapter> chapters;



}
