package com.example.soup.deepstudy.entity;

import java.time.LocalDateTime;

import com.example.soup.schedule.entity.Schedule;
import com.example.soup.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "deep_studies")
public class DeepStudy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 강결합 User 참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 강결합 Schedule 참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false)
	private Schedule schedule;

	private String topicTitle;

	@Column(columnDefinition = "TEXT")
	private String content;

	private LocalDateTime submittedAt;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
