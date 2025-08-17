package com.example.soup.deepstudy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.example.soup.schedule.entity.Schedule;
import com.example.soup.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deep_studies")
public class DeepStudy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false)
	private Schedule schedule;

	private String topicTitle;     // 심화학습 주제명

	@Column(columnDefinition = "TEXT")
	private String contentUrl;     //  심화학습 링크

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static DeepStudy create(User user, Schedule schedule, String topicTitle, String contentUrl) {
		DeepStudy deepstudy = new DeepStudy();
		deepstudy.user = user;
		deepstudy.schedule = schedule;
		deepstudy.topicTitle = topicTitle;
		deepstudy.contentUrl = contentUrl;
		deepstudy.createdAt = LocalDateTime.now();
		return deepstudy;
	}

	public void update(String topicTitle, String contentUrl) {
		this.topicTitle = topicTitle;
		this.contentUrl = contentUrl;
		this.updatedAt = LocalDateTime.now();
	}
}
