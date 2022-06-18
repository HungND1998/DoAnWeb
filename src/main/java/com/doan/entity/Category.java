package com.doan.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @Column(name = "CREATED_AT")
    private LocalDateTime createAt;

    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @Column(name = "STATUS")
    private boolean status;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
    List<Story> stories;

}
