package com.example.soup.study.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;
	private String type;
	private String period;

	private Boolean isCompleted;
	private LocalDateTime createdAt;

	// 생성 팩토리
	public static Study create(String name, String description, String type, String period) {
		Study study = new Study();
		study.name = name;
		study.description = description;
		study.type = type;
		study.period = period;
		study.isCompleted = false;
		study.createdAt = LocalDateTime.now();
		return study;
	}

	// 업데이트 로직
	public void update(String name, String description, String type, String period, Boolean isCompleted) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.period = period;
		this.isCompleted = isCompleted;
	}
}

