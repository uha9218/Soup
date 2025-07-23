package com.example.soup.admin.section;

import com.example.soup.admin.section.controller.AdminSectionController;
import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AdminSectionControllerUnitTest {

	private final AdminSectionController controller = new AdminSectionController();

	@Test
	@DisplayName("섹션 생성 - 유닛 테스트 성공")
	void createSection_unit_success() {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(1L)
			.sectionName("섹션1")
			.studyId(100L)
			.build();

		ResponseEntity<AdminSectionResponseDTO> response = controller.createSection(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminSectionResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getSectionNumber()).isEqualTo(1L);
		assertThat(body.getSectionName()).isEqualTo("섹션1");
	}

	@Test
	@DisplayName("섹션 수정 - 유닛 테스트 성공")
	void updateSection_unit_success() {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(1L)
			.sectionName("수정된 섹션")
			.studyId(100L)
			.build();

		ResponseEntity<AdminSectionResponseDTO> response = controller.updateSection(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminSectionResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getSectionName()).isEqualTo("수정된 섹션");
	}

	@Test
	@DisplayName("섹션 조회 - 유닛 테스트 성공")
	void getSection_unit_success() {
		ResponseEntity<AdminSectionResponseDTO> response = controller.getSection();

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminSectionResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getSectionNumber()).isEqualTo(1L);
		assertThat(body.getSectionName()).isEqualTo("섹션 이름");
	}

	@Test
	@DisplayName("섹션 삭제 - 유닛 테스트 성공")
	void deleteSection_unit_success() {
		Long sectionNumber = 1L;
		String sectionName = "섹션1";

		ResponseEntity<AdminSectionDeleteResponseDTO> response = controller.deleteSection(sectionNumber, sectionName);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminSectionDeleteResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getSectionNumber()).isEqualTo(sectionNumber);
		assertThat(body.getSectionName()).isEqualTo(sectionName);
	}
}
