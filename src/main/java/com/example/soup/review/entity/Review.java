package com.example.soup.review.entity;

import java.time.LocalDateTime;

import com.example.soup.section.entity.Section;
import com.example.soup.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id")
	private Section section;
	private String content;     // 직접 입력 회고
	private String reviewUrl;   // 블로그 등 외부 회고 링크
	private LocalDateTime submittedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static Review create(User user, Section section, String content, String reviewUrl) {
		Review review = new Review();
		review.user = user;
		review.section = section;
		review.content = content;
		review.reviewUrl = reviewUrl;
		review.createdAt = LocalDateTime.now();
		review.updatedAt = LocalDateTime.now();
		return review;
	}

	public void update(String content, String reviewUrl) {
		this.content = content;
		this.reviewUrl = reviewUrl;
		this.updatedAt = LocalDateTime.now();
	}
}
