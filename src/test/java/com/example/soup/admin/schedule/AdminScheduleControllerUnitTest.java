package com.example.soup.admin.schedule;

import com.example.soup.admin.schedule.controller.AdminScheduleController;
import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminScheduleControllerUnitTest {

	private final AdminScheduleController controller = new AdminScheduleController();

	@Test
	@DisplayName("일정 생성 - 유닛 테스트 성공")
	void createSchedule_unit_success() {
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("스터디 일정")
			.type("온라인")
			.studyId(100L)
			.sectionIds(List.of(1L, 2L))
			.meetingLink("https://zoom.us/example")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		ResponseEntity<AdminScheduleResponseDTO> response = controller.createSchedule(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminScheduleResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getTitle()).isEqualTo("스터디 일정");
	}

	@Test
	@DisplayName("일정 수정 - 유닛 테스트 성공")
	void updateSchedule_unit_success() {
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("수정된 일정")
			.type("오프라인")
			.studyId(100L)
			.sectionIds(List.of(3L, 4L))
			.meetingLink("https://maps.example.com/location")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		ResponseEntity<AdminScheduleResponseDTO> response = controller.updateSchedule(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminScheduleResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getTitle()).isEqualTo("수정된 일정");
	}

	@Test
	@DisplayName("일정 조회 - 유닛 테스트 성공")
	void getSchedule_unit_success() {
		ResponseEntity<AdminScheduleResponseDTO> response = controller.getSchedule();

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminScheduleResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getTitle()).isEqualTo("더미 일정");
	}

	@Test
	@DisplayName("일정 삭제 - 유닛 테스트 성공")
	void deleteSchedule_unit_success() {
		String title = "삭제할 일정";

		ResponseEntity<AdminScheduleDeleteResponseDTO> response = controller.deleteSchedule(title);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		AdminScheduleDeleteResponseDTO body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getTitle()).isEqualTo(title);
	}
}
