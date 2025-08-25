package com.example.soup.schedule;

import com.example.soup.schedule.dto.ScheduleCalendarRequestDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 빠른 검증을 위한 간단한 테스트 클래스
 * 개발 중 빠른 피드백을 받기 위한 기본적인 기능 테스트만 포함
 */
@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ScheduleCalendarQuickTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Study testStudy;
    private Section testSection;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 테스트 데이터 초기화
        scheduleRepository.deleteAll();
        sectionRepository.deleteAll();
        studyRepository.deleteAll();

        // 테스트용 Study 생성
        testStudy = Study.create(
                "빠른 테스트 스터디",
                "빠른 테스트용 스터디입니다",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        testStudy = studyRepository.save(testStudy);

        // 테스트용 Section 생성
        testSection = Section.create(1L, "빠른 테스트 섹션", testStudy, null, true);
        testSection = sectionRepository.save(testSection);
    }

    @Test
    @DisplayName("기본 기능 테스트 - 정상적인 일정 조회")
    void getMonthlySchedules_basicFunctionality() throws Exception {
        // given - 기본 테스트 데이터 생성
        Schedule schedule = Schedule.create(
                testStudy,
                "테스트 일정",
                "테스트용 일정입니다",
                LocalDateTime.of(2025, 3, 15, 14, 30),
                "https://zoom.us/test",
                true,
                List.of(testSection)
        );
        scheduleRepository.save(schedule);
        
        // when - 3월 데이터 조회
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(3)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 기본 기능 검증
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules").isArray())
                .andExpect(jsonPath("$.schedules.length()").value(1))
                .andExpect(jsonPath("$.schedules[0].title").value("테스트 일정"))
                .andExpect(jsonPath("$.schedules[0].date").value("2025-03-15"))
                .andExpect(jsonPath("$.schedules[0].time").value("14:30"));
    }

    @Test
    @DisplayName("빈 데이터 테스트 - 해당 월에 일정이 없는 경우")
    void getMonthlySchedules_emptyData() throws Exception {
        // when - 4월 데이터 조회 (일정 없음)
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(4)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 빈 배열 반환 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules").isArray())
                .andExpect(jsonPath("$.schedules.length()").value(0));
    }

    @Test
    @DisplayName("에러 처리 테스트 - 잘못된 년도")
    void getMonthlySchedules_invalidYear() throws Exception {
        // when - 잘못된 년도로 요청
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(1899) // 잘못된 년도
                .month(3)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 400 Bad Request 응답 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("에러 처리 테스트 - 잘못된 월")
    void getMonthlySchedules_invalidMonth() throws Exception {
        // when - 잘못된 월로 요청
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(13) // 잘못된 월
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 400 Bad Request 응답 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
