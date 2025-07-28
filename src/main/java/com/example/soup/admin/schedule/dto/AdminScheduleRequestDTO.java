package com.example.soup.admin.schedule.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminScheduleRequestDTO {

	private final String title;
	private final String type; // 온라인 / 오프라인
	private final Long studyId;
	private final List<Long> sectionIds;
	private final String meetingLink; // 장소 or 화상회의 링크
	private final LocalDateTime scheduleDateTime;

	@Builder
	public AdminScheduleRequestDTO(
		@JsonProperty("title") String title,
		@JsonProperty("type") String type,
		@JsonProperty("studyId") Long studyId,
		@JsonProperty("sectionIds") List<Long> sectionIds,
		@JsonProperty("meetingLink") String meetingLink,
		@JsonProperty("scheduleDateTime") LocalDateTime scheduleDateTime
	) {
		this.title = title;
		this.type = type;
		this.studyId = studyId;
		this.sectionIds = sectionIds;
		this.meetingLink = meetingLink;
		this.scheduleDateTime = scheduleDateTime;
	}
}
