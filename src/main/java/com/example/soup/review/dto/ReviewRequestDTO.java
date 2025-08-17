package com.example.soup.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

public class ReviewRequestDTO {
	@Getter
	public static class Create {
		private final Long userId;      // 작성자
		private final Long sectionId;   // 어느 Section에 대한 회고인지
		private final String content;   // 회고 텍스트 (nullable)
		private final String reviewUrl; // 외부 회고 링크 (nullable)

		@Builder
		public Create(
			@JsonProperty("userId") Long userId,
			@JsonProperty("sectionId") Long sectionId,
			@JsonProperty("content") String content,
			@JsonProperty("reviewUrl") String reviewUrl
		) {
			this.userId = userId;
			this.sectionId = sectionId;
			this.content = content;
			this.reviewUrl = reviewUrl;
		}
	}

	@Getter
	public static class Update {
		private final String content;
		private final String reviewUrl;

		@Builder
		public Update(
			@JsonProperty("content") String content,
			@JsonProperty("reviewUrl") String reviewUrl
		) {
			this.content = content;
			this.reviewUrl = reviewUrl;
		}
	}
}
