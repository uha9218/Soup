package com.example.soup.admin.schedule;


import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import com.example.soup.admin.schedule.service.AdminScheduleService;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminScheduleServiceUnitTest {

	private ScheduleRepository scheduleRepository;
	private AdminScheduleService scheduleService;

	@BeforeEach
	void setUp() {
		scheduleRepository = mock(ScheduleRepository.class);
		scheduleService = new AdminScheduleService(scheduleRepository);
	}

	@Test
	@DisplayName("스케줄 생성 성공")
	void createSchedule_success() {
		// given
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("HTTP 스터디 일정")
			.type("온라인")
			.studyId(100L)
			.sectionIds(List.of(1L, 2L))
			.meetingLink("https://zoom.us/example")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		Schedule dummy = Schedule.create(
			request.getTitle(),
			request.getType(),
			request.getStudyId(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);

		when(scheduleRepository.save(any(Schedule.class))).thenReturn(dummy);

		// when
		AdminScheduleResponseDTO response = scheduleService.createSchedule(request);

		// then
		assertThat(response.getTitle()).isEqualTo("HTTP 스터디 일정");
		assertThat(response.getType()).isEqualTo("온라인");
		assertThat(response.getSectionIds()).containsExactly(1L, 2L);
		verify(scheduleRepository).save(any(Schedule.class));
	}

	@Test
	@DisplayName("스케줄 조회 실패 - 존재하지 않음")
	void getSchedule_notFound() {
		when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> scheduleService.getSchedule(1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 스케줄이 존재하지 않습니다.");
	}

	@Test
	@DisplayName("스케줄 수정 성공")
	void updateSchedule_success() {
		Schedule schedule = Schedule.create(
			"기존 일정", "오프라인", 100L, List.of(1L), "강의실 A", LocalDateTime.now()
		);

		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("변경된 일정")
			.type("온라인")
			.studyId(100L)
			.sectionIds(List.of(2L, 3L))
			.meetingLink("https://zoom.us/new")
			.scheduleDateTime(LocalDateTime.now().plusDays(1))
			.build();

		when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

		AdminScheduleResponseDTO response = scheduleService.updateSchedule(1L, request);

		assertThat(response.getTitle()).isEqualTo("변경된 일정");
		assertThat(response.getSectionIds()).containsExactly(2L, 3L);
	}

	@Test
	@DisplayName("스케줄 삭제 성공")
	void deleteSchedule_success() {
		Schedule schedule = Schedule.create("삭제 일정", "오프라인", 100L, List.of(1L), "장소", LocalDateTime.now());

		when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

		AdminScheduleDeleteResponseDTO response = scheduleService.deleteSchedule(1L);

		assertThat(response.getTitle()).isEqualTo("삭제 일정");
		verify(scheduleRepository).delete(schedule);
	}
}