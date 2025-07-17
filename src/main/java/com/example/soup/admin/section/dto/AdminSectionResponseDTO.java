package com.example.soup.admin.section.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminSectionResponseDTO {

	private final Long sectionNumber;
	private final String sectionName;
	private final Long studyId;

	@Builder
	public AdminSectionResponseDTO(Long sectionNumber, String sectionName, Long studyId) {
		this.sectionNumber = sectionNumber;
		this.sectionName = sectionName;
		this.studyId = studyId;
	}

	public static AdminSectionResponseDTO of(Long sectionNumber, String sectionName, Long studyId) {
		return AdminSectionResponseDTO.builder()
			.sectionNumber(sectionNumber)
			.sectionName(sectionName)
			.studyId(studyId)
			.build();
	}
}
