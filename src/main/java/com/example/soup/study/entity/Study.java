package com.example.soup.study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

	private Boolean completed;  // 스터디 완료 여부

	private LocalDateTime createdAt;  // 생성 시각
	private LocalDateTime updatedAt;  // 수정 시각

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
		study.createdAt = LocalDateTime.now();
		return study;
	}

	// 업데이트 로직 (startDate, endDate, completed 추가)
	public void update(String name, String description, String type,
		LocalDateTime startDate, LocalDateTime endDate, Boolean completed) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.completed = completed;
		this.updatedAt = LocalDateTime.now();
	}

	public void setActualEndDate(LocalDateTime actualEndDate) {
		this.actualEndDate = actualEndDate;
		this.updatedAt = LocalDateTime.now(); // 수정 시각도 함께 갱신
	}

}
