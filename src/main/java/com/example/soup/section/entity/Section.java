package com.example.soup.section.entity;

import com.example.soup.study.entity.Study;

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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id")
	private Study study;

	public static Section create(Long sectionNumber, String sectionName, Study study) {
		Section section = new Section();
		section.sectionNumber = sectionNumber;
		section.sectionName = sectionName;
		section.study = study;
		return section;
	}

	public void update(Long sectionNumber, String sectionName) {
		this.sectionNumber = sectionNumber;
		this.sectionName = sectionName;
	}
}