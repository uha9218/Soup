package com.example.soup.study.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;	//스터디 제목
	private String description;	//스터디 설명
	private String type;	//스터디 유형(책 혹은 강의 등)
	private String period;	//예상 기간
	private Date startDate;	//스터디 시작 날짜
	private Date endDate;	//스터디 예상 종료 날짜
	private Boolean isCompleted;	//스터디 완료 여부
	private LocalDateTime createdAt;	//스터디 생성 시각

	// 생성 팩토리
	public static Study create(String name, String description, String type, String period) {
		Study study = new Study();
		study.name = name;
		study.description = description;
		study.type = type;
		study.period = period;
		study.isCompleted = false;
		study.createdAt = LocalDateTime.now();
		return study;
	}

	// 업데이트 로직
	public void update(String name, String description, String type, String period, Boolean isCompleted) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.period = period;
		this.isCompleted = isCompleted;
	}
}

