package com.example.soup.admin.study;

import com.example.soup.admin.study.controller.AdminStudyController;
import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.example.soup.admin.study.dto.AdminStudyResponseDTO;
import com.example.soup.admin.study.dto.AdminStudyDeleteResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AdminStudyControllerUnitTest {

	private final AdminStudyController controller = new AdminStudyController();

	@Test
	@DisplayName("스터디 생성 - 유닛 테스트 성공")
	void createStudy_unit_success() {
		AdminStudyRequestDTO.Create request = AdminStudyRequestDTO.Create.builder()
			.name("Spring Study")
			.description("Spring Boot Mastery")
			.type("온라인")
			.period("4 months")
			.build();

		ResponseEntity<AdminStudyResponseDTO> response = controller.createStudy(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminStudyResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getName()).isEqualTo("Spring Study");
		assertThat(body.getDescription()).isEqualTo("Spring Boot Mastery");
		assertThat(body.getType()).isEqualTo("온라인");
		assertThat(body.getPeriod()).isEqualTo("4 months");
		assertThat(body.getIsCompleted()).isFalse();
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

		ResponseEntity<AdminStudyResponseDTO> response = controller.updateStudy(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminStudyResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getName()).isEqualTo("Updated Study");
		assertThat(body.getIsCompleted()).isTrue();
	}

	@Test
	@DisplayName("스터디 조회 - 유닛 테스트 성공")
	void getStudy_unit_success() {
		ResponseEntity<AdminStudyResponseDTO> response = controller.getStudy();

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminStudyResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getName()).isEqualTo("Dummy Study");
	}

	@Test
	@DisplayName("스터디 삭제 - 유닛 테스트 성공")
	void deleteStudy_unit_success() {
		String name = "Spring Study";

		ResponseEntity<AdminStudyDeleteResponseDTO> response = controller.deleteStudy(name);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminStudyDeleteResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getName()).isEqualTo(name);
	}
}
