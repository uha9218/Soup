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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ScheduleCalendarFunctionalTest {

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
                "기능 테스트 스터디",
                "기능 테스트용 스터디입니다",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        testStudy = studyRepository.save(testStudy);

        // 테스트용 Section 생성
        testSection = Section.create(1L, "기능 테스트 섹션", testStudy, null, true);
        testSection = sectionRepository.save(testSection);
    }

    /**
     * 헬퍼 메서드: 지정된 개수만큼 일정 데이터 생성 및 저장
     */
    private void setupSchedules(int count, int year, int month, String prefix) {
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // 월별 최대 일수 계산
            int maxDays = getMaxDaysInMonth(year, month);
            int day = 1 + (i % maxDays);
            
            Schedule schedule = Schedule.create(
                    testStudy,
                    prefix + " " + String.format("%04d", i),
                    "테스트용 일정 설명 " + i,
                    LocalDateTime.of(year, month, day, (i % 24), (i % 60)),
                    "https://zoom.us/test" + i,
                    i % 2 == 0,
                    List.of(testSection)
            );
            schedules.add(schedule);
        }
        scheduleRepository.saveAll(schedules);
    }
    
    /**
     * 월별 최대 일수 계산 헬퍼 메서드
     */
    private int getMaxDaysInMonth(int year, int month) {
        switch (month) {
            case 2:
                return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
            case 4, 6, 9, 11:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * 헬퍼 메서드: 특정 날짜에 일정 데이터 생성 및 저장
     */
    private void setupSchedulesOnSpecificDate(int count, int year, int month, int day, String prefix) {
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Schedule schedule = Schedule.create(
                    testStudy,
                    prefix + " " + String.format("%04d", i),
                    "테스트용 일정 설명 " + i,
                    LocalDateTime.of(year, month, day, (i % 24), (i % 60)),
                    "https://zoom.us/test" + i,
                    i % 2 == 0,
                    List.of(testSection)
            );
            schedules.add(schedule);
        }
        scheduleRepository.saveAll(schedules);
    }

    @Test
    @DisplayName("대용량 데이터 통합 테스트 - 1,000개 일정 생성 후 조회")
    void getMonthlySchedules_largeDataset() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 대용량 데이터 생성 (1,000개 일정으로 축소)
        int largeDatasetSize = 1000;
        setupSchedules(largeDatasetSize, 2025, 8, "대용량 테스트 일정");
        
        // 데이터베이스에 실제로 저장되었는지 확인
        assertThat(scheduleRepository.count()).isEqualTo(largeDatasetSize);
        
        // when - API 호출
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 응답 검증
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules").isArray())
                .andExpect(jsonPath("$.schedules.length()").value(largeDatasetSize))
                .andExpect(jsonPath("$.schedules[0].title").exists())
                .andExpect(jsonPath("$.schedules[999].title").exists());
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("=== 대용량 데이터 기능 테스트 시간: " + totalTime + "ms ===");
    }

    @Test
    @DisplayName("경계값 통합 테스트 - 2월 말일 데이터")
    void getMonthlySchedules_boundaryValue_endOfMonth() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 2025년 2월 말일(28일)에 데이터 생성
        int boundaryDatasetSize = 100; // 기능 테스트용으로 축소
        setupSchedulesOnSpecificDate(boundaryDatasetSize, 2025, 2, 28, "경계값 테스트 일정");
        
        // when - 2월 데이터 조회
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(2)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 경계값에서 정상 동작 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules.length()").value(boundaryDatasetSize))
                .andExpect(jsonPath("$.schedules[0].date").value("2025-02-28"))
                .andExpect(jsonPath("$.schedules[99].date").value("2025-02-28"));
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("2월 말일 경계값 테스트 시간: " + totalTime + "ms");
    }

    @Test
    @DisplayName("경계값 통합 테스트 - 윤년 2월 29일 데이터")
    void getMonthlySchedules_boundaryValue_leapYear() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 2024년(윤년) 2월 29일에 데이터 생성
        int leapYearDatasetSize = 50; // 기능 테스트용으로 축소
        setupSchedulesOnSpecificDate(leapYearDatasetSize, 2024, 2, 29, "윤년 테스트 일정");
        
        // when - 2024년 2월 데이터 조회
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2024)
                .month(2)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 윤년 경계값에서 정상 동작 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules.length()").value(leapYearDatasetSize))
                .andExpect(jsonPath("$.schedules[0].date").value("2024-02-29"))
                .andExpect(jsonPath("$.schedules[49].date").value("2024-02-29"));
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("윤년 2월 29일 경계값 테스트 시간: " + totalTime + "ms");
    }

    @Test
    @DisplayName("경계값 통합 테스트 - 연도 경계 (12월 31일)")
    void getMonthlySchedules_boundaryValue_yearEnd() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 2025년 12월 31일에 데이터 생성
        int yearEndDatasetSize = 200; // 기능 테스트용으로 축소
        setupSchedulesOnSpecificDate(yearEndDatasetSize, 2025, 12, 31, "연말 테스트 일정");
        
        // when - 12월 데이터 조회
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(12)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 연말 경계값에서 정상 동작 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules.length()").value(yearEndDatasetSize))
                .andExpect(jsonPath("$.schedules[0].date").value("2025-12-31"))
                .andExpect(jsonPath("$.schedules[199].date").value("2025-12-31"));
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("연도 경계값 테스트 시간: " + totalTime + "ms");
    }

    @Test
    @DisplayName("대용량 데이터 환경에서의 에러 처리 테스트")
    void getMonthlySchedules_errorHandlingWithLargeData() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 대용량 데이터 생성
        int errorTestSize = 300; // 기능 테스트용으로 축소
        setupSchedules(errorTestSize, 2025, 6, "에러 테스트 일정");
        
        // when & then - 잘못된 요청으로 에러 처리 테스트
        ScheduleCalendarRequestDTO invalidRequest = ScheduleCalendarRequestDTO.builder()
                .year(1899) // 잘못된 년도
                .month(6)
                .build();

        String invalidRequestJson = objectMapper.writeValueAsString(invalidRequest);

        // 에러 응답이 400 Bad Request로 처리되는지 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
        
        // 정상 요청은 여전히 동작하는지 확인
        ScheduleCalendarRequestDTO validRequest = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(6)
                .build();

        String validRequestJson = objectMapper.writeValueAsString(validRequest);

        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules.length()").value(errorTestSize));
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("에러 처리 테스트 시간: " + totalTime + "ms");
    }

    @Test
    @DisplayName("기본 기능 테스트 - 정상적인 일정 조회")
    void getMonthlySchedules_basicFunctionality() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 기본 테스트 데이터 생성
        setupSchedules(10, 2025, 3, "기본 테스트 일정");
        
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
                .andExpect(jsonPath("$.schedules.length()").value(10))
                .andExpect(jsonPath("$.schedules[0].title").exists())
                .andExpect(jsonPath("$.schedules[0].date").value("2025-03-01"))
                .andExpect(jsonPath("$.schedules[0].time").exists())
                .andExpect(jsonPath("$.schedules[0].description").exists())
                .andExpect(jsonPath("$.schedules[0].meetingLocation").exists());
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("기본 기능 테스트 시간: " + totalTime + "ms");
    }

    @Test
    @DisplayName("빈 데이터 테스트 - 해당 월에 일정이 없는 경우")
    void getMonthlySchedules_emptyData() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 4월에만 일정 생성
        setupSchedules(5, 2025, 4, "4월 테스트 일정");
        
        // when - 5월 데이터 조회 (일정 없음)
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(5)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // then - 빈 배열 반환 확인
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules").isArray())
                .andExpect(jsonPath("$.schedules.length()").value(0));
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("빈 데이터 테스트 시간: " + totalTime + "ms");
    }
}
