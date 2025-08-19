package com.example.soup.section.entity;

import com.example.soup.study.entity.Study;
import com.example.soup.schedule.entity.Schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sectionNumber;
	private String sectionName;
	
	@Column(name = "needs_review", nullable = false)
	private Boolean needsReview;   // 회고 필요 여부
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id")
	private Study study;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id")
	private Schedule schedule;

	public static Section create(Long sectionNumber, String sectionName, Study study, Schedule schedule, Boolean needsReview) {
		Section section = new Section();
		section.sectionNumber = sectionNumber;
		section.sectionName = sectionName;
		section.study = study;
		section.schedule = schedule;
		section.needsReview = needsReview != null ? needsReview : true;
		return section;
	}

	public static Section create(Long sectionNumber, String sectionName, Study study) {
		return create(sectionNumber, sectionName, study, null, true);
	}

	public void update(Long sectionNumber, String sectionName, Boolean needsReview) {
		this.sectionNumber = sectionNumber;
		this.sectionName = sectionName;
		this.needsReview = needsReview != null ? needsReview : true;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}