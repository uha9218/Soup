package com.example.soup.admin.section.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminSectionRequestDTO {

	private final Long sectionNumber;
	private final String sectionName;
	private final Long studyId;

	@Builder
	public AdminSectionRequestDTO(
		@JsonProperty("sectionNumber") Long sectionNumber,
		@JsonProperty("sectionName") String sectionName,
		@JsonProperty("studyId") Long studyId
	) {
		this.sectionNumber = sectionNumber;
		this.sectionName = sectionName;
		this.studyId = studyId;
	}
}