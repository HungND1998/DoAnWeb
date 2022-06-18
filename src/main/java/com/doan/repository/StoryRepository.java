package com.doan.repository;

import com.doan.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {

    List<Story> findAllByUserId(Integer userId);

    List<Story> findTop10ByStatusOrderByCreatedAtDesc(boolean status);

    List<Story> findTop4ByStatusOrderByCreatedAtDesc(boolean status);

    List<Story> findTop4ByStatusOrderByViewDesc(boolean status);


}
