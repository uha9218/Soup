package com.example.soup.schedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.soup.section.entity.Section;
import com.example.soup.study.entity.Study;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id", nullable = false)
	private Study study;

	@Column(nullable = false)
	private String name;         // 일정명

	private String description;  // 일정 상세 설명

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "schedule_date", nullable = false)
	private LocalDateTime scheduleDate;   // 일정 날짜
	@Column(name = "meeting_location")
	private String meetingLocation;   // 미팅 장소 혹은 온라인 미팅 링크

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "schedule")
	private List<Section> sections = new ArrayList<>();

	public static Schedule create(
		Study study,
		String name,
		String description,
		LocalDateTime scheduleDate,
		String meetingLocation,
		List<Section> sections
	) {
		Schedule schedule = new Schedule();
		schedule.study = study;
		schedule.name = name;
		schedule.description = description;
		schedule.scheduleDate = scheduleDate;
		schedule.meetingLocation = meetingLocation;
		schedule.createdAt = LocalDateTime.now();
		if (sections != null) {
			schedule.sections.addAll(sections);
		}
		return schedule;
	}
	public void update(
		String name,
		String description,
		LocalDateTime scheduleDate,
		String meetingLocation,
		List<Section> sections
	) {
		this.name = name;
		this.description = description;
		this.scheduleDate = scheduleDate;
		this.meetingLocation = meetingLocation;
		this.updatedAt = LocalDateTime.now();

		// 연관 Section 변경 처리
		if (sections != null) {
			this.sections.clear();
			this.sections.addAll(sections);
		}
	}

}
