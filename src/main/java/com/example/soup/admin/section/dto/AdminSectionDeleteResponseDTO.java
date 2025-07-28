package com.example.soup.admin.section.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminSectionDeleteResponseDTO {

	private final Long sectionNumber;
	private final String sectionName;

	@Builder
	public AdminSectionDeleteResponseDTO(Long sectionNumber, String sectionName) {
		this.sectionNumber = sectionNumber;
		this.sectionName = sectionName;
	}

	public static AdminSectionDeleteResponseDTO of(Long sectionNumber, String sectionName) {
		return AdminSectionDeleteResponseDTO.builder()
			.sectionNumber(sectionNumber)
			.sectionName(sectionName)
			.build();
	}
}