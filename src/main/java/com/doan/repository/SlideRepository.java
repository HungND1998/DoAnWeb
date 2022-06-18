package com.doan.repository;

import com.doan.entity.Slide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Integer> {
}
