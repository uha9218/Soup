package com.example.soup.admin.section.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

public class AdminSectionRequestDTO {

	@Getter
	public static class Create {
		private final Long sectionNumber;
		private final String sectionName;
		private final Long studyId;

		@Builder
		public Create(
			@JsonProperty("sectionNumber") Long sectionNumber,
			@JsonProperty("sectionName") String sectionName,
			@JsonProperty("studyId") Long studyId
		) {
			this.sectionNumber = sectionNumber;
			this.sectionName = sectionName;
			this.studyId = studyId;
		}
	}

	@Getter
	public static class Update {
		private final Long sectionNumber;
		private final String sectionName;

		@Builder
		public Update(
			@JsonProperty("sectionNumber") Long sectionNumber,
			@JsonProperty("sectionName") String sectionName
		) {
			this.sectionNumber = sectionNumber;
			this.sectionName = sectionName;
		}
	}
}
