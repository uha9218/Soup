package com.example.soup.deepstudy.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeepStudyResponseDTO {

	private final Long id;

	private final Long userId;
	private final String userName;

	private final Long scheduleId;
	private final String scheduleName;

	private final String topicTitle;
	private final String contentUrl;

	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@Builder
	public DeepStudyResponseDTO(
		Long id,
		Long userId,
		String userName,
		Long scheduleId,
		String scheduleName,
		String topicTitle,
		String contentUrl,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.scheduleId = scheduleId;
		this.scheduleName = scheduleName;
		this.topicTitle = topicTitle;
		this.contentUrl = contentUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static DeepStudyResponseDTO of(
		Long id,
		Long userId,
		String userName,
		Long scheduleId,
		String scheduleName,
		String topicTitle,
		String contentUrl,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		return DeepStudyResponseDTO.builder()
			.id(id)
			.userId(userId)
			.userName(userName)
			.scheduleId(scheduleId)
			.scheduleName(scheduleName)
			.topicTitle(topicTitle)
			.contentUrl(contentUrl)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
