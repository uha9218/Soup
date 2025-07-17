package com.example.soup.admin.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminStudyRequestDTO {
	@Getter
	public static class Create {
		private final String name;
		private final String description;
		private final String type;
		private final String period;

		@Builder
		public Create(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("type") String type,
			@JsonProperty("period") String period) {
			this.name = name;
			this.description = description;
			this.type = type;
			this.period = period;
		}
	}

	@Getter
	public static class Update {
		private final String name;
		private final String description;
		private final String type;
		private final String period;
		private final Boolean isCompleted;

		@Builder
		public Update(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("type") String type,
			@JsonProperty("period") String period,
			@JsonProperty("isCompleted") Boolean isCompleted) {
			this.name = name;
			this.description = description;
			this.type = type;
			this.period = period;
			this.isCompleted = isCompleted;
		}
	}
}
