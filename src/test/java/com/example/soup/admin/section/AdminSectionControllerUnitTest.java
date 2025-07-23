package com.example.soup.admin.section.controller;

import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.service.AdminSectionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

class AdminSectionControllerUnitTest {

	private AdminSectionService sectionService;
	private AdminSectionController controller;

	@BeforeEach
	void setUp() {
		sectionService = mock(AdminSectionService.class);
		controller = new AdminSectionController(sectionService);
	}

	@Test
	@DisplayName("섹션 생성 - 유닛 테스트 성공")
	void createSection_success() {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(1L)
			.sectionName("섹션 A")
			.studyId(10L)
			.build();

		AdminSectionResponseDTO mockResponse = AdminSectionResponseDTO.of(1L, "섹션 A", 10L);

		when(sectionService.createSection(request)).thenReturn(mockResponse);

		ResponseEntity<AdminSectionResponseDTO> response = controller.createSection(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("전체 섹션 조회 - 유닛 테스트 성공")
	void getAllSections_unit_success() {
		// given
		List<AdminSectionResponseDTO> mockList = List.of(
			AdminSectionResponseDTO.of(1L, "섹션 1", 100L),
			AdminSectionResponseDTO.of(2L, "섹션 2", 100L)
		);

		when(sectionService.getAllSections()).thenReturn(mockList);

		// when
		ResponseEntity<List<AdminSectionResponseDTO>> response = controller.getAllSections();

		// then
		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody()).extracting("sectionName")
			.containsExactly("섹션 1", "섹션 2");
	}

	@Test
	@DisplayName("섹션 조회 - 유닛 테스트 성공")
	void getSection_success() {
		AdminSectionResponseDTO mockResponse = AdminSectionResponseDTO.of(1L, "섹션 A", 10L);

		when(sectionService.getSection(1L)).thenReturn(mockResponse);

		ResponseEntity<AdminSectionResponseDTO> response = controller.getSection(1L);

		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("섹션 수정 - 유닛 테스트 성공")
	void updateSection_success() {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(2L)
			.sectionName("수정된 섹션")
			.studyId(10L)
			.build();

		AdminSectionResponseDTO mockResponse = AdminSectionResponseDTO.of(2L, "수정된 섹션", 10L);

		when(sectionService.updateSection(1L, request)).thenReturn(mockResponse);

		ResponseEntity<AdminSectionResponseDTO> response = controller.updateSection(1L, request);

		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("섹션 삭제 - 유닛 테스트 성공")
	void deleteSection_success() {
		AdminSectionDeleteResponseDTO mockResponse = AdminSectionDeleteResponseDTO.of(1L, "삭제 섹션");

		when(sectionService.deleteSection(1L)).thenReturn(mockResponse);

		ResponseEntity<AdminSectionDeleteResponseDTO> response = controller.deleteSection(1L);

		assertThat(response.getBody()).isEqualTo(mockResponse);
	}
}
