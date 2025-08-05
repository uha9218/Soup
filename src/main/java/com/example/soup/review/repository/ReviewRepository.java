package com.example.soup.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soup.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
