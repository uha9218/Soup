package com.example.soup.admin.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminScheduleDeleteResponseDTO {

	private final String title;

	@Builder
	public AdminScheduleDeleteResponseDTO(String title) {
		this.title = title;
	}

	public static AdminScheduleDeleteResponseDTO of(String title) {
		return AdminScheduleDeleteResponseDTO.builder()
			.title(title)
			.build();
	}
}
