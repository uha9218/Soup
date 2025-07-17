package com.example.soup.admin.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminStudyResponseDTO {
	private final String name;
	private final String description;
	private final String type;
	private final String period;
	private final Boolean isCompleted;
	private final LocalDateTime createdAt;

	@Builder
	public AdminStudyResponseDTO(String name, String description, String type, String period, Boolean isCompleted, LocalDateTime createdAt) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.period = period;
		this.isCompleted = isCompleted;
		this.createdAt = createdAt;
	}

	public static AdminStudyResponseDTO of(String name, String description, String type, String period, Boolean isCompleted, LocalDateTime createdAt) {
		return AdminStudyResponseDTO.builder()
			.name(name)
			.description(description)
			.type(type)
			.period(period)
			.isCompleted(isCompleted)
			.createdAt(createdAt)
			.build();
	}
}
