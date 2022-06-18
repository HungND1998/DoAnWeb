package com.doan.repository;

import com.doan.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    @Query(value = "FROM Chapter c where c.story.id = ?1 and c.status = true")
    List<Chapter> findByStoryActive(Integer storyId);

    List<Chapter> findByStoryId(Integer storyId);
}
