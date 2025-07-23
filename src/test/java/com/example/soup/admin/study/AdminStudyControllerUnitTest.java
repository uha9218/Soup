package com.example.soup.admin.study;

import com.example.soup.admin.study.controller.AdminStudyController;
import com.example.soup.admin.study.dto.AdminStudyDeleteResponseDTO;
import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.example.soup.admin.study.dto.AdminStudyResponseDTO;
import com.example.soup.admin.study.service.AdminStudyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminStudyControllerUnitTest {

	private AdminStudyService studyService;
	private AdminStudyController controller;

	@BeforeEach
	void setUp() {
		studyService = mock(AdminStudyService.class);
		controller = new AdminStudyController(studyService);
	}

	@Test
	@DisplayName("스터디 생성 - 유닛 테스트 성공")
	void createStudy_unit_success() {
		AdminStudyRequestDTO.Create request = AdminStudyRequestDTO.Create.builder()
			.name("Spring Study")
			.description("Spring Boot Mastery")
			.type("온라인")
			.period("4 months")
			.build();

		AdminStudyResponseDTO mockResponse = AdminStudyResponseDTO.of(
			"Spring Study", "Spring Boot Mastery", "온라인", "4 months", false, null
		);

		when(studyService.createStudy(request)).thenReturn(mockResponse);

		ResponseEntity<AdminStudyResponseDTO> response = controller.createStudy(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("스터디 수정 - 유닛 테스트 성공")
	void updateStudy_unit_success() {
		AdminStudyRequestDTO.Update request = AdminStudyRequestDTO.Update.builder()
			.name("Updated Study")
			.description("Updated Description")
			.type("오프라인")
			.period("2 months")
			.isCompleted(true)
			.build();

		AdminStudyResponseDTO mockResponse = AdminStudyResponseDTO.of(
			"Updated Study", "Updated Description", "오프라인", "2 months", true, null
		);

		when(studyService.updateStudy(eq(1L), eq(request))).thenReturn(mockResponse);

		ResponseEntity<AdminStudyResponseDTO> response = controller.updateStudy(1L, request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("스터디 조회 - 유닛 테스트 성공")
	void getStudy_unit_success() {
		AdminStudyResponseDTO mockResponse = AdminStudyResponseDTO.of(
			"Dummy Study", "설명", "온라인", "4개월", false, null
		);

		when(studyService.getStudy(1L)).thenReturn(mockResponse);

		ResponseEntity<AdminStudyResponseDTO> response = controller.getStudy(1L);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("스터디 삭제 - 유닛 테스트 성공")
	void deleteStudy_unit_success() {
		AdminStudyDeleteResponseDTO mockResponse = AdminStudyDeleteResponseDTO.of("Spring Study");

		when(studyService.deleteStudy(1L)).thenReturn(mockResponse);

		ResponseEntity<AdminStudyDeleteResponseDTO> response = controller.deleteStudy(1L);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(mockResponse);
	}
}
