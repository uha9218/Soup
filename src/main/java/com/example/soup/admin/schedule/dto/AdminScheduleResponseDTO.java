package com.example.soup.admin.schedule.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminScheduleResponseDTO {

	private final Long id;
	private final Long studyId;
	private final String name;
	private final String description;
	private final LocalDateTime scheduleDate;
	private final String meetingLocation;
	private final List<Long> sectionIds;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@Builder
	public AdminScheduleResponseDTO(
		Long id,
		Long studyId,
		String name,
		String description,
		LocalDateTime scheduleDate,
		String meetingLocation,
		List<Long> sectionIds,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		this.id = id;
		this.studyId = studyId;
		this.name = name;
		this.description = description;
		this.scheduleDate = scheduleDate;
		this.meetingLocation = meetingLocation;
		this.sectionIds = sectionIds;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static AdminScheduleResponseDTO of(
		Long id,
		Long studyId,
		String name,
		String description,
		LocalDateTime scheduleDate,
		String meetingLocation,
		List<Long> sectionIds,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		return AdminScheduleResponseDTO.builder()
			.id(id)
			.studyId(studyId)
			.name(name)
			.description(description)
			.scheduleDate(scheduleDate)
			.meetingLocation(meetingLocation)
			.sectionIds(sectionIds)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}