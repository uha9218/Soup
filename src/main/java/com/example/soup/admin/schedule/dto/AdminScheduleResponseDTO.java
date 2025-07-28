package com.example.soup.admin.schedule.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminScheduleResponseDTO {

	private final String title;
	private final String type;
	private final Long studyId;
	private final List<Long> sectionIds;
	private final String meetingLink;
	private final LocalDateTime scheduleDateTime;

	@Builder
	public AdminScheduleResponseDTO(String title, String type, Long studyId, List<Long> sectionIds, String meetingLink, LocalDateTime scheduleDateTime) {
		this.title = title;
		this.type = type;
		this.studyId = studyId;
		this.sectionIds = sectionIds;
		this.meetingLink = meetingLink;
		this.scheduleDateTime = scheduleDateTime;
	}

	public static AdminScheduleResponseDTO of(String title, String type, Long studyId, List<Long> sectionIds, String meetingLink, LocalDateTime scheduleDateTime) {
		return AdminScheduleResponseDTO.builder()
			.title(title)
			.type(type)
			.studyId(studyId)
			.sectionIds(sectionIds)
			.meetingLink(meetingLink)
			.scheduleDateTime(scheduleDateTime)
			.build();
	}
}