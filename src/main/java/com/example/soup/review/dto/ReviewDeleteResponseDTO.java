package com.example.soup.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewDeleteResponseDTO {

	private final String sectionName;

	@Builder
	public ReviewDeleteResponseDTO(String sectionName) {
		this.sectionName = sectionName;
	}

	public static ReviewDeleteResponseDTO of(String sectionName) {
		return ReviewDeleteResponseDTO.builder()
			.sectionName(sectionName)
			.build();
	}
}

