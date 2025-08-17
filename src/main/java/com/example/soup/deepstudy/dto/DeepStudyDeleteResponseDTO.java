package com.example.soup.deepstudy.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class DeepStudyDeleteResponseDTO {

	private final String scheduleName;
	private final String topicTitle;

	@Builder
	public DeepStudyDeleteResponseDTO(String scheduleName, String topicTitle) {
		this.scheduleName = scheduleName;
		this.topicTitle = topicTitle;
	}

	public static DeepStudyDeleteResponseDTO of(String scheduleName, String topicTitle) {
		return DeepStudyDeleteResponseDTO.builder()
			.scheduleName(scheduleName)
			.topicTitle(topicTitle)
			.build();
	}
}

