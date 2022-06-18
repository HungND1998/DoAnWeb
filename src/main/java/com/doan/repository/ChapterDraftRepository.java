package com.doan.repository;

import com.doan.entity.ChapterDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChapterDraftRepository extends JpaRepository<ChapterDraft, Integer> {
    @Query(value = "select u from ChapterDraft u where u.status = 'PENDING'")
    List<ChapterDraft> findByChapterDraftPending();

    @Query(value = "select u from ChapterDraft u where u.status = 'PENDING' AND u.story.id = ?1")
    List<ChapterDraft> findByChapterDraftPendingStoryId(Integer storyId);
}
