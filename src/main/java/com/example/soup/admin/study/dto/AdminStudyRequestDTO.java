package com.example.soup.admin.study.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private final LocalDateTime startDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private final LocalDateTime endDate;

		@Builder
		public Create(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("type") String type,
			@JsonProperty("startDate") LocalDateTime startDate,
			@JsonProperty("endDate") LocalDateTime endDate
		) {
			this.name = name;
			this.description = description;
			this.type = type;
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}

	@Getter
	public static class Update {
		private final String name;
		private final String description;
		private final String type;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private final LocalDateTime startDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private final LocalDateTime endDate;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		private final LocalDateTime actualEndDate;

		private final Boolean completed;

		@Builder
		public Update(
			@JsonProperty("name") String name,
			@JsonProperty("description") String description,
			@JsonProperty("type") String type,
			@JsonProperty("startDate") LocalDateTime startDate,
			@JsonProperty("endDate") LocalDateTime endDate,
			@JsonProperty("actualEndDate") LocalDateTime actualEndDate,
			@JsonProperty("completed") Boolean completed
		) {
			this.name = name;
			this.description = description;
			this.type = type;
			this.startDate = startDate;
			this.endDate = endDate;
			this.actualEndDate = actualEndDate;
			this.completed = completed;
		}
	}
}