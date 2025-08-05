package com.example.soup.admin.schedule.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
public class AdminScheduleRequestDTO {

	@Getter
	public static class Create {
		private final Long studyId;          // 해당 스터디 ID
		private final String name;           // 일정명
		private final String description;    // 일정 설명

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private final LocalDateTime scheduleDate; // 일정 날짜/시간

		private final String meetingLocation;     // 미팅 장소/링크

		private final List<Long> sectionIds;      // 연결될 Section ID 리스트

		@Builder
		public Create(
			@JsonProperty("studyId") Long studyId,
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("scheduleDate") LocalDateTime scheduleDate,
			@JsonProperty("meetingLocation") String meetingLocation,
			@JsonProperty("sectionIds") List<Long> sectionIds
		) {
			this.studyId = studyId;
			this.name = name;
			this.description = description;
			this.scheduleDate = scheduleDate;
			this.meetingLocation = meetingLocation;
			this.sectionIds = sectionIds;
		}
	}

	@Getter
	public static class Update {
		private final String name;
		private final String description;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private final LocalDateTime scheduleDate;

		private final String meetingLocation;
		private final List<Long> sectionIds;

		@Builder
		public Update(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("scheduleDate") LocalDateTime scheduleDate,
			@JsonProperty("meetingLocation") String meetingLocation,
			@JsonProperty("sectionIds") List<Long> sectionIds
		) {
			this.name = name;
			this.description = description;
			this.scheduleDate = scheduleDate;
			this.meetingLocation = meetingLocation;
			this.sectionIds = sectionIds;
		}
	}
}
