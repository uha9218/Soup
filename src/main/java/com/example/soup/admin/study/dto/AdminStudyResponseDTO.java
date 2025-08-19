package com.example.soup.admin.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class AdminStudyResponseDTO {
	private final String name;
	private final String description;
	private final String type;

	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final LocalDateTime actualEndDate;

	private final Boolean completed;
	private final Boolean isActive;
	private final LocalDateTime createdAt;

	@Builder
	public AdminStudyResponseDTO(
		String name,
		String description,
		String type,
		LocalDateTime startDate,
		LocalDateTime endDate,
		LocalDateTime actualEndDate,
		Boolean completed,
		Boolean isActive,
		LocalDateTime createdAt) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.actualEndDate = actualEndDate;
		this.completed = completed;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}

	public static AdminStudyResponseDTO of(
		String name,
		String description,
		String type,
		LocalDateTime startDate,
		LocalDateTime endDate,
		LocalDateTime actualEndDate,
		Boolean completed,
		Boolean isActive,
		LocalDateTime createdAt) {
		return AdminStudyResponseDTO.builder()
			.name(name)
			.description(description)
			.type(type)
			.startDate(startDate)
			.endDate(endDate)
			.actualEndDate(actualEndDate)
			.completed(completed)
			.isActive(isActive)
			.createdAt(createdAt)
			.build();
	}
}
