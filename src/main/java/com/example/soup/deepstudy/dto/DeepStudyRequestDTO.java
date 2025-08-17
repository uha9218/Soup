package com.example.soup.deepstudy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DeepStudyRequestDTO {

	@Getter
	public static class Create {
		private final Long userId;
		private final Long scheduleId;
		private final String topicTitle;
		private final String contentUrl;

		@Builder
		public Create(
			@JsonProperty("userId") Long userId,
			@JsonProperty("scheduleId") Long scheduleId,
			@JsonProperty("topicTitle") String topicTitle,
			@JsonProperty("contentUrl") String contentUrl
		) {
			this.userId = userId;
			this.scheduleId = scheduleId;
			this.topicTitle = topicTitle;
			this.contentUrl = contentUrl;
		}
	}

	@Getter
	public static class Update {
		private final String topicTitle;
		private final String contentUrl;

		@Builder
		public Update(
			@JsonProperty("topicTitle") String topicTitle,
			@JsonProperty("contentUrl") String contentUrl
		) {
			this.topicTitle = topicTitle;
			this.contentUrl = contentUrl;
		}
	}
}

