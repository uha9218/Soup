package com.example.soup.schedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String type;
	private Long studyId;
	private String meetingLink;
	private LocalDateTime scheduleDateTime;

	@ElementCollection
	@CollectionTable(name = "schedule_section", joinColumns = @JoinColumn(name = "schedule_id"))
	@Column(name = "section_id")
	private List<Long> sectionIds = new ArrayList<>();

	private Schedule(
		String title,
		String type,
		Long studyId,
		List<Long> sectionIds,
		String meetingLink,
		LocalDateTime scheduleDateTime
	) {
		this.title = title;
		this.type = type;
		this.studyId = studyId;
		this.sectionIds = sectionIds;
		this.meetingLink = meetingLink;
		this.scheduleDateTime = scheduleDateTime;
	}

	public static Schedule create(String title, String type, Long studyId, List<Long> sectionIds, String meetingLink, LocalDateTime scheduleDateTime) {
		return new Schedule(title, type, studyId, sectionIds, meetingLink, scheduleDateTime);
	}

	public void update(String title, String type, List<Long> sectionIds, String meetingLink, LocalDateTime scheduleDateTime) {
		this.title = title;
		this.type = type;
		this.sectionIds = sectionIds;
		this.meetingLink = meetingLink;
		this.scheduleDateTime = scheduleDateTime;
	}
}
