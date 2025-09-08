package com.example.soup.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soup.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySectionId(Long sectionId);
    List<Review> findBySectionIdIn(List<Long> sectionIds);
}
