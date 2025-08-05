package com.example.soup.review.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDTO {

	private final Long id;
	private final Long userId;
	private final String userName;     // 사용자 이름/닉네임
	private final Long sectionId;
	private final String sectionName;  // 섹션 이름
	private final String content;      // 회고 본문
	private final String reviewUrl;    // 외부 회고 링크

	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@Builder
	public ReviewResponseDTO(
		Long id,
		Long userId,
		String userName,
		Long sectionId,
		String sectionName,
		String content,
		String reviewUrl,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.content = content;
		this.reviewUrl = reviewUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// of 메서드도 추가하면 편리
	public static ReviewResponseDTO of(
		Long id,
		Long userId,
		String userName,
		Long sectionId,
		String sectionName,
		String content,
		String reviewUrl,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		return ReviewResponseDTO.builder()
			.id(id)
			.userId(userId)
			.userName(userName)
			.sectionId(sectionId)
			.sectionName(sectionName)
			.content(content)
			.reviewUrl(reviewUrl)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
