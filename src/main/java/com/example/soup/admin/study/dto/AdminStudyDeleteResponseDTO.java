package com.example.soup.admin.study.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminStudyDeleteResponseDTO {
	private final String name;

	@Builder
	public AdminStudyDeleteResponseDTO(String name) {
		this.name = name;
	}

	public static AdminStudyDeleteResponseDTO of(String name) {
		return AdminStudyDeleteResponseDTO.builder()
			.name(name)
			.build();
	}
}
