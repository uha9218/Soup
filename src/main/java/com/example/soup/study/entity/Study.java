package com.example.soup.study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;        // 스터디 제목
	private String description; // 스터디 설명
	private String type;        // 스터디 유형(책 혹은 강의 등)

	private LocalDateTime startDate; // 스터디 시작 날짜
	private LocalDateTime endDate;   // 스터디 예상 종료 날짜
	private LocalDateTime actualEndDate;	//스터디 실제 종료 날짜

	private boolean completed;  // 스터디 완료 여부
	private boolean isActive;   // 현재 진행 중인 스터디 여부

	private LocalDateTime createdAt;  // 생성 시각
	private LocalDateTime updatedAt;  // 수정 시각

	@OneToMany(mappedBy = "study")
	private Set<com.example.soup.schedule.entity.Schedule> schedules = new HashSet<>();

	// 생성 팩토리 (startDate, endDate 추가)
	public static Study create(String name, String description, String type,
		LocalDateTime startDate, LocalDateTime endDate) {
		Study study = new Study();
		study.name = name;
		study.description = description;
		study.type = type;
		study.startDate = startDate;
		study.endDate = endDate;
		study.completed = false;
		study.isActive = true; // 기본적으로 활성 상태로 생성
		study.createdAt = LocalDateTime.now();
		return study;
	}

	// 업데이트 로직 (startDate, endDate, completed 추가)
	public void update(String name, String description, String type,
		LocalDateTime startDate, LocalDateTime endDate, boolean completed, boolean isActive) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.completed = completed;
		this.isActive = isActive;
		this.updatedAt = LocalDateTime.now();
	}

	public void setActualEndDate(LocalDateTime actualEndDate) {
		this.actualEndDate = actualEndDate;
		this.updatedAt = LocalDateTime.now(); // 수정 시각도 함께 갱신
	}

}
