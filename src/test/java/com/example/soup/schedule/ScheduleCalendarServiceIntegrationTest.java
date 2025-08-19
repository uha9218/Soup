package com.example.soup.schedule;

import com.example.soup.schedule.dto.ScheduleCalendarRequestDTO;
import com.example.soup.schedule.dto.ScheduleCalendarResponseDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import com.example.soup.schedule.service.ScheduleCalendarService;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ScheduleCalendarServiceIntegrationTest {

    @Autowired
    private ScheduleCalendarService scheduleCalendarService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private Study testStudy;
    private Section testSection;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        scheduleRepository.deleteAll();
        sectionRepository.deleteAll();
        studyRepository.deleteAll();

        // 테스트용 Study 생성
        testStudy = Study.create(
                "테스트 스터디",
                "테스트용 스터디입니다",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        testStudy = studyRepository.save(testStudy);

        // 테스트용 Section 생성
        testSection = Section.create(1L, "테스트 섹션", testStudy);
        testSection = sectionRepository.save(testSection);
    }

    @Test
    @DisplayName("특정 연월의 일정 조회 성공 - 통합 테스트")
    void getMonthlySchedules_success() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        // 테스트 데이터 생성
        Schedule schedule1 = Schedule.create(
                testStudy, "스프링 MVC 1편 온라인 미팅", "설명1",
                LocalDateTime.of(2025, 8, 16, 20, 0), "https://zoom.us/example1", List.of(testSection)
        );
        Schedule schedule2 = Schedule.create(
                testStudy, "스터디 회고 세션", "설명2",
                LocalDateTime.of(2025, 8, 20, 15, 0), "https://zoom.us/example2", List.of(testSection)
        );

        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

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
    }

    @Test
    @DisplayName("해당 월에 일정이 없는 경우 - 통합 테스트")
    void getMonthlySchedules_emptyList() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(9)
                .build();

        // 8월에만 일정 생성
        Schedule schedule = Schedule.create(
                testStudy, "8월 일정", "설명",
                LocalDateTime.of(2025, 8, 15, 10, 0), "장소", List.of(testSection)
        );
        scheduleRepository.save(schedule);

        // when
        ScheduleCalendarResponseDTO result = scheduleCalendarService.getMonthlySchedules(request);

        // then
        assertThat(result.getSchedules()).isEmpty();
    }

    @Test
    @DisplayName("일정이 있는 경우 정확한 개수 반환 - 통합 테스트")
    void getMonthlySchedules_correctCount() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        // 8월에 3개 일정 생성
        List<Schedule> schedules = List.of(
                Schedule.create(testStudy, "일정1", "설명1", LocalDateTime.of(2025, 8, 1, 10, 0), "장소1", List.of(testSection)),
                Schedule.create(testStudy, "일정2", "설명2", LocalDateTime.of(2025, 8, 15, 14, 0), "장소2", List.of(testSection)),
                Schedule.create(testStudy, "일정3", "설명3", LocalDateTime.of(2025, 8, 31, 20, 0), "장소3", List.of(testSection))
        );

        scheduleRepository.saveAll(schedules);

        // when
        ScheduleCalendarResponseDTO result = scheduleCalendarService.getMonthlySchedules(request);

        // then
        assertThat(result.getSchedules()).hasSize(3);
        assertThat(result.getSchedules().get(0).getTitle()).isEqualTo("일정1");
        assertThat(result.getSchedules().get(1).getTitle()).isEqualTo("일정2");
        assertThat(result.getSchedules().get(2).getTitle()).isEqualTo("일정3");
    }

    @Test
    @DisplayName("잘못된 연월 입력 - 년도 범위 초과 - 통합 테스트")
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
    }

    @Test
    @DisplayName("잘못된 연월 입력 - 월 범위 초과 - 통합 테스트")
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
    }

    @Test
    @DisplayName("null 파라미터 처리 - 년도 null - 통합 테스트")
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
    }

    @Test
    @DisplayName("Repository 쿼리 검증 - 통합 테스트")
    void verifyRepositoryQuery() {
        // given
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        // 8월과 9월에 각각 일정 생성
        Schedule augustSchedule = Schedule.create(
                testStudy, "8월 일정", "설명",
                LocalDateTime.of(2025, 8, 15, 10, 0), "장소", List.of(testSection)
        );
        Schedule septemberSchedule = Schedule.create(
                testStudy, "9월 일정", "설명",
                LocalDateTime.of(2025, 9, 15, 10, 0), "장소", List.of(testSection)
        );

        scheduleRepository.save(augustSchedule);
        scheduleRepository.save(septemberSchedule);

        // when
        ScheduleCalendarResponseDTO result = scheduleCalendarService.getMonthlySchedules(request);

        // then
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getTitle()).isEqualTo("8월 일정");
    }
}