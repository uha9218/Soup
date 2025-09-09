package com.example.soup.schedule;

import com.example.soup.schedule.dto.ScheduleCalendarRequestDTO;
import com.example.soup.schedule.dto.ScheduleCalendarResponseDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import com.example.soup.schedule.service.ScheduleCalendarService;
import com.example.soup.section.entity.Section;
import com.example.soup.study.entity.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleCalendarServiceUnitTest {

    private ScheduleRepository scheduleRepository;
    private ScheduleCalendarService scheduleCalendarService;

    @BeforeEach
    void setUp() {
        scheduleRepository = mock(ScheduleRepository.class);
        scheduleCalendarService = new ScheduleCalendarService(scheduleRepository);
    }

    @Test
    @DisplayName("특정 연월의 일정 조회 성공")
    void getMonthlySchedules_success() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        Study study = mock(Study.class);
        Section section = mock(Section.class);

        Schedule schedule1 = Schedule.create(
                study, "스프링 MVC 1편 온라인 미팅", "설명1",
                LocalDateTime.of(2025, 8, 16, 20, 0), "https://zoom.us/example1", true, List.of(section)
        );
        Schedule schedule2 = Schedule.create(
                study, "스터디 회고 세션", "설명2",
                LocalDateTime.of(2025, 8, 20, 15, 0), "https://zoom.us/example2", false, List.of(section)
        );

        when(scheduleRepository.findByScheduleDateBetweenOrderByScheduleDate(
                LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 8, 31, 23, 59, 59, 999999999)
        )).thenReturn(Arrays.asList(schedule1, schedule2));

        // when
        ScheduleCalendarResponseDTO result = scheduleCalendarService.getMonthlySchedules(request);

        // then
        assertThat(result.getSchedules()).hasSize(2);
        assertThat(result.getSchedules().get(0).getTitle()).isEqualTo("스프링 MVC 1편 온라인 미팅");
        assertThat(result.getSchedules().get(0).getDate()).isEqualTo("2025-08-16");
        assertThat(result.getSchedules().get(0).getTime()).isEqualTo("20:00");
        assertThat(result.getSchedules().get(1).getTitle()).isEqualTo("스터디 회고 세션");
        assertThat(result.getSchedules().get(1).getDate()).isEqualTo("2025-08-20");
        assertThat(result.getSchedules().get(1).getTime()).isEqualTo("15:00");

        verify(scheduleRepository).findByScheduleDateBetweenOrderByScheduleDate(
                LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 8, 31, 23, 59, 59, 999999999)
        );
    }

    @Test
    @DisplayName("해당 월에 일정이 없는 경우")
    void getMonthlySchedules_emptyList() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(9)
                .build();

        when(scheduleRepository.findByScheduleDateBetweenOrderByScheduleDate(
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 9, 30, 23, 59, 59, 999999999)
        )).thenReturn(Collections.emptyList());

        // when
        ScheduleCalendarResponseDTO result = scheduleCalendarService.getMonthlySchedules(request);

        // then
        assertThat(result.getSchedules()).isEmpty();
        verify(scheduleRepository).findByScheduleDateBetweenOrderByScheduleDate(
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 9, 30, 23, 59, 59, 999999999)
        );
    }

    @Test
    @DisplayName("일정이 있는 경우 정확한 개수 반환")
    void getMonthlySchedules_correctCount() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        Study study = mock(Study.class);
        Section section = mock(Section.class);

        List<Schedule> schedules = Arrays.asList(
                Schedule.create(study, "일정1", "설명1", LocalDateTime.of(2025, 8, 1, 10, 0), "장소1", true, List.of(section)),
                Schedule.create(study, "일정2", "설명2", LocalDateTime.of(2025, 8, 15, 14, 0), "장소2", false, List.of(section)),
                Schedule.create(study, "일정3", "설명3", LocalDateTime.of(2025, 8, 31, 20, 0), "장소3", true, List.of(section))
        );

        when(scheduleRepository.findByScheduleDateBetweenOrderByScheduleDate(
                LocalDateTime.of(2025, 8, 1, 0, 0),
                LocalDateTime.of(2025, 8, 31, 23, 59, 59, 999999999)
        )).thenReturn(schedules);

        // when
        ScheduleCalendarResponseDTO result = scheduleCalendarService.getMonthlySchedules(request);

        // then
        assertThat(result.getSchedules()).hasSize(3);
        assertThat(result.getSchedules().get(0).getTitle()).isEqualTo("일정1");
        assertThat(result.getSchedules().get(1).getTitle()).isEqualTo("일정2");
        assertThat(result.getSchedules().get(2).getTitle()).isEqualTo("일정3");
    }

    @Test
    @DisplayName("잘못된 연월 입력 - 년도 범위 초과")
    void getMonthlySchedules_invalidYear() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(1899)
                .month(8)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleCalendarService.getMonthlySchedules(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("년도는 1900년부터 2100년 사이여야 합니다.");

        verify(scheduleRepository, never()).findByScheduleDateBetweenOrderByScheduleDate(any(), any());
    }

    @Test
    @DisplayName("잘못된 연월 입력 - 월 범위 초과")
    void getMonthlySchedules_invalidMonth() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(13)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleCalendarService.getMonthlySchedules(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("월은 1월부터 12월 사이여야 합니다.");

        verify(scheduleRepository, never()).findByScheduleDateBetweenOrderByScheduleDate(any(), any());
    }

    @Test
    @DisplayName("잘못된 연월 입력 - 월 범위 미만")
    void getMonthlySchedules_invalidMonthZero() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(0)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleCalendarService.getMonthlySchedules(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("월은 1월부터 12월 사이여야 합니다.");

        verify(scheduleRepository, never()).findByScheduleDateBetweenOrderByScheduleDate(any(), any());
    }

    @Test
    @DisplayName("null 파라미터 처리 - 년도 null")
    void getMonthlySchedules_nullYear() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(null)
                .month(8)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleCalendarService.getMonthlySchedules(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("년도와 월은 필수 입력값입니다.");

        verify(scheduleRepository, never()).findByScheduleDateBetweenOrderByScheduleDate(any(), any());
    }

    @Test
    @DisplayName("null 파라미터 처리 - 월 null")
    void getMonthlySchedules_nullMonth() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(null)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleCalendarService.getMonthlySchedules(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("년도와 월은 필수 입력값입니다.");

        verify(scheduleRepository, never()).findByScheduleDateBetweenOrderByScheduleDate(any(), any());
    }

    @Test
    @DisplayName("null 파라미터 처리 - 년도와 월 모두 null")
    void getMonthlySchedules_bothNull() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(null)
                .month(null)
                .build();

        // when & then
        assertThatThrownBy(() -> scheduleCalendarService.getMonthlySchedules(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("년도와 월은 필수 입력값입니다.");

        verify(scheduleRepository, never()).findByScheduleDateBetweenOrderByScheduleDate(any(), any());
    }
}
