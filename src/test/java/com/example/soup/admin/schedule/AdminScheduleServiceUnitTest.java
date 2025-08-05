package com.example.soup.admin.schedule;

import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import com.example.soup.admin.schedule.service.AdminScheduleService;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

class AdminScheduleServiceUnitTest {

	private ScheduleRepository scheduleRepository;
	private StudyRepository studyRepository;
	private SectionRepository sectionRepository;
	private AdminScheduleService scheduleService;

	@BeforeEach
	void setUp() {
		scheduleRepository = mock(ScheduleRepository.class);
		studyRepository = mock(StudyRepository.class);
		sectionRepository = mock(SectionRepository.class);
		scheduleService = new AdminScheduleService(scheduleRepository, studyRepository, sectionRepository);
	}

	@Test
	@DisplayName("스케줄 생성 성공")
	void createSchedule_success() {
		// given
		Long studyId = 100L;
		List<Long> sectionIds = List.of(1L, 2L);
		Study study = mock(Study.class);
		Section section1 = mock(Section.class);
		Section section2 = mock(Section.class);

		when(study.getId()).thenReturn(studyId);
		when(section1.getId()).thenReturn(1L);
		when(section2.getId()).thenReturn(2L);

		AdminScheduleRequestDTO.Create request = AdminScheduleRequestDTO.Create.builder()
			.studyId(studyId)
			.name("HTTP 스터디 일정")
			.description("설명")
			.scheduleDate(LocalDateTime.now())
			.meetingLocation("https://zoom.us/example")
			.sectionIds(sectionIds)
			.build();

		when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
		when(sectionRepository.findAllById(sectionIds)).thenReturn(List.of(section1, section2));

		Schedule dummy = Schedule.create(
			study,
			request.getName(),
			request.getDescription(),
			request.getScheduleDate(),
			request.getMeetingLocation(),
			List.of(section1, section2)
		);
		when(scheduleRepository.save(any(Schedule.class))).thenReturn(dummy);

		// when
		AdminScheduleResponseDTO response = scheduleService.createSchedule(request);

		// then
		assertThat(response.getName()).isEqualTo("HTTP 스터디 일정");
		assertThat(response.getSectionIds()).containsExactly(1L, 2L);
		verify(scheduleRepository).save(any(Schedule.class));
		verify(studyRepository).findById(studyId);
		verify(sectionRepository).findAllById(sectionIds);
	}

	@Test
	@DisplayName("전체 스케줄 조회 - 유닛 테스트 성공")
	void getAllSchedules_success() {
		// given
		Study study = mock(Study.class);
		when(study.getId()).thenReturn(1L);

		Section s1_1 = mock(Section.class); when(s1_1.getId()).thenReturn(1L);
		Section s1_2 = mock(Section.class); when(s1_2.getId()).thenReturn(2L);
		Section s2_1 = mock(Section.class); when(s2_1.getId()).thenReturn(3L);

		Schedule sch1 = Schedule.create(
			study, "스터디1 일정", "desc1",
			LocalDateTime.of(2025, 8, 1, 19, 0), "장소1", List.of(s1_1, s1_2)
		);
		Schedule sch2 = Schedule.create(
			study, "스터디2 일정", "desc2",
			LocalDateTime.of(2025, 8, 2, 20, 0), "장소2", List.of(s2_1)
		);
		when(scheduleRepository.findAll()).thenReturn(List.of(sch1, sch2));

		// when
		List<AdminScheduleResponseDTO> result = scheduleService.getAllSchedules();

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("스터디1 일정");
		assertThat(result.get(0).getSectionIds()).containsExactly(1L, 2L);
		assertThat(result.get(1).getName()).isEqualTo("스터디2 일정");
		assertThat(result.get(1).getSectionIds()).containsExactly(3L);
	}

	@Test
	@DisplayName("스케줄 조회 실패 - 존재하지 않음")
	void getSchedule_notFound() {
		when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> scheduleService.getSchedule(1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 일정이 존재하지 않습니다.");
	}

	@Test
	@DisplayName("스케줄 수정 성공")
	void updateSchedule_success() {
		// given
		Study study = mock(Study.class);
		when(study.getId()).thenReturn(100L);

		Section oldSection = mock(Section.class); when(oldSection.getId()).thenReturn(1L);
		Section newSection1 = mock(Section.class); when(newSection1.getId()).thenReturn(2L);
		Section newSection2 = mock(Section.class); when(newSection2.getId()).thenReturn(3L);

		Schedule schedule = Schedule.create(
			study, "기존 일정", "old", LocalDateTime.now(), "장소", List.of(oldSection)
		);

		List<Long> newSectionIds = List.of(2L, 3L);

		AdminScheduleRequestDTO.Update request = AdminScheduleRequestDTO.Update.builder()
			.name("변경된 일정")
			.description("updated desc")
			.scheduleDate(LocalDateTime.now().plusDays(1))
			.meetingLocation("https://zoom.us/new")
			.sectionIds(newSectionIds)
			.build();

		when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
		when(sectionRepository.findAllById(newSectionIds)).thenReturn(List.of(newSection1, newSection2));

		// when
		AdminScheduleResponseDTO response = scheduleService.updateSchedule(1L, request);

		// then
		assertThat(response.getName()).isEqualTo("변경된 일정");
		assertThat(response.getSectionIds()).containsExactly(2L, 3L);
	}

	@Test
	@DisplayName("스케줄 삭제 성공")
	void deleteSchedule_success() {
		Study study = mock(Study.class); when(study.getId()).thenReturn(100L);
		Section section = mock(Section.class); when(section.getId()).thenReturn(1L);

		Schedule schedule = Schedule.create(
			study, "삭제 일정", "desc", LocalDateTime.now(), "장소", List.of(section)
		);

		when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

		AdminScheduleDeleteResponseDTO response = scheduleService.deleteSchedule(1L);

		assertThat(response.getTitle()).isEqualTo("삭제 일정");
		verify(scheduleRepository).delete(schedule);
	}
}
