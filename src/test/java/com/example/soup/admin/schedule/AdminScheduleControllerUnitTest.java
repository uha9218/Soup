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

import com.example.soup.admin.schedule.service.AdminScheduleService;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

class AdminScheduleControllerUnitTest {

	private AdminScheduleService scheduleService;
	private AdminScheduleController controller;

	@BeforeEach
	void setUp() {
		scheduleService = mock(AdminScheduleService.class);
		controller = new AdminScheduleController(scheduleService);
	}

	@Test
	@DisplayName("스케줄 생성 - 유닛 테스트 성공")
	void createSchedule_success() {
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("스터디 일정")
			.type("온라인")
			.studyId(1L)
			.sectionIds(List.of(1L, 2L))
			.meetingLink("https://zoom.us")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		AdminScheduleResponseDTO mockResponse = AdminScheduleResponseDTO.of(
			request.getTitle(),
			request.getType(),
			request.getStudyId(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);

		when(scheduleService.createSchedule(request)).thenReturn(mockResponse);

		ResponseEntity<AdminScheduleResponseDTO> response = controller.createSchedule(request);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("스케줄 조회 성공")
	void getSchedule_success() {
		AdminScheduleResponseDTO mockResponse = AdminScheduleResponseDTO.of(
			"HTTP 일정", "온라인", 1L, List.of(1L, 2L), "https://zoom.us", LocalDateTime.now()
		);

		when(scheduleService.getSchedule(1L)).thenReturn(mockResponse);

		ResponseEntity<AdminScheduleResponseDTO> response = controller.getSchedule(1L);

		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("스케줄 수정 성공")
	void updateSchedule_success() {
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("변경된 제목")
			.type("오프라인")
			.studyId(1L)
			.sectionIds(List.of(3L))
			.meetingLink("강의실 B")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		AdminScheduleResponseDTO mockResponse = AdminScheduleResponseDTO.of(
			request.getTitle(),
			request.getType(),
			request.getStudyId(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);

		when(scheduleService.updateSchedule(1L, request)).thenReturn(mockResponse);

		ResponseEntity<AdminScheduleResponseDTO> response = controller.updateSchedule(1L, request);

		assertThat(response.getBody()).isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("스케줄 삭제 성공")
	void deleteSchedule_success() {
		AdminScheduleDeleteResponseDTO mockResponse = AdminScheduleDeleteResponseDTO.of("삭제된 일정");

		when(scheduleService.deleteSchedule(1L)).thenReturn(mockResponse);

		ResponseEntity<AdminScheduleDeleteResponseDTO> response = controller.deleteSchedule(1L);

		assertThat(response.getBody()).isEqualTo(mockResponse);
	}
}