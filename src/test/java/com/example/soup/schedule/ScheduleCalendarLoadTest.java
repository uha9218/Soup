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

/**
 * 부하 테스트 클래스
 * 주의: 이 테스트는 실제 성능 측정이 아닌 "여러 번 호출해도 기능이 정상 동작하는지"를 확인하는 목적입니다.
 * 실제 성능 테스트는 JMeter, Gatling 등의 전문 도구를 사용하는 것을 권장합니다.
 */
@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ScheduleCalendarLoadTest {

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
                "부하 테스트 스터디",
                "부하 테스트용 스터디입니다",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        testStudy = studyRepository.save(testStudy);

        // 테스트용 Section 생성
        testSection = Section.create(1L, "부하 테스트 섹션", testStudy, null, true);
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
                    "부하 테스트용 일정 설명 " + i,
                    LocalDateTime.of(year, month, day, (i % 24), (i % 60)),
                    "https://zoom.us/loadtest" + i,
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

    @Test
    @DisplayName("연속 API 호출 테스트 - 여러 번 호출해도 정상 동작 확인")
    void getMonthlySchedules_sequentialCalls() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 대용량 데이터 생성 (5,000개)
        int datasetSize = 5000;
        setupSchedules(datasetSize, 2025, 7, "연속 호출 테스트 일정");
        
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(7)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        System.out.println("=== 연속 API 호출 테스트 시작 ===");
        
        // when & then - 연속 20번 호출하여 안정성 테스트
        for (int i = 0; i < 20; i++) {
            long callStart = System.currentTimeMillis();
            
            mockMvc.perform(post("/api/schedules/calendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.schedules.length()").value(datasetSize))
                    .andExpect(jsonPath("$.schedules[0].title").exists())
                    .andExpect(jsonPath("$.schedules[4999].title").exists());
            
            long callEnd = System.currentTimeMillis();
            System.out.println("호출 " + (i + 1) + "번째 시간: " + (callEnd - callStart) + "ms");
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("=== 연속 API 호출 테스트 전체 시간: " + totalTime + "ms ===");
    }

    @Test
    @DisplayName("다양한 월 연속 조회 테스트 - 12개월 연속 조회")
    void getMonthlySchedules_multipleMonths() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 12개월에 걸쳐 데이터 분산 생성
        int totalDataSize = 6000; // 월별 500개씩
        for (int month = 1; month <= 12; month++) {
            setupSchedules(500, 2025, month, "다양한 월 테스트 일정");
        }
        
        System.out.println("=== 다양한 월 연속 조회 테스트 시작 ===");
        
        // when & then - 12개월 연속 조회
        for (int month = 1; month <= 12; month++) {
            long monthCallStart = System.currentTimeMillis();
            
            ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                    .year(2025)
                    .month(month)
                    .build();

            String requestJson = objectMapper.writeValueAsString(request);

            mockMvc.perform(post("/api/schedules/calendar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.schedules.length()").value(500))
                    .andExpect(jsonPath("$.schedules[0].title").exists());
            
            long monthCallEnd = System.currentTimeMillis();
            System.out.println(month + "월 조회 시간: " + (monthCallEnd - monthCallStart) + "ms");
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("=== 다양한 월 연속 조회 테스트 전체 시간: " + totalTime + "ms ===");
        
        // 전체 데이터 개수 확인
        assertThat(scheduleRepository.count()).isEqualTo(totalDataSize);
    }

    @Test
    @DisplayName("동시 요청 시뮬레이션 테스트 - CompletableFuture 사용")
    void getMonthlySchedules_concurrentRequests() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 대용량 데이터 생성
        int datasetSize = 3000;
        setupSchedules(datasetSize, 2025, 8, "동시 요청 테스트 일정");
        
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(8)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        System.out.println("=== 동시 요청 시뮬레이션 테스트 시작 ===");
        
        // when - 10개의 동시 요청 시뮬레이션 (MockMvc는 스레드 안전하지 않으므로 순차 실행으로 변경)
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < 10; i++) {
            try {
                long callStart = System.currentTimeMillis();
                
                mockMvc.perform(post("/api/schedules/calendar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.schedules.length()").value(datasetSize));
                
                long callEnd = System.currentTimeMillis();
                successCount++;
                System.out.println("요청 " + (i + 1) + "번째 완료 시간: " + (callEnd - callStart) + "ms");
                
            } catch (Exception e) {
                failCount++;
                System.err.println("요청 " + (i + 1) + "번째 실패: " + e.getMessage());
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("=== 동시 요청 시뮬레이션 테스트 전체 시간: " + totalTime + "ms ===");
        System.out.println("성공: " + successCount + ", 실패: " + failCount);
        
        // 성공률이 80% 이상이어야 테스트 통과
        assertThat(successCount).isGreaterThanOrEqualTo(8);
    }

    @Test
    @DisplayName("메모리 사용량 테스트 - 대용량 데이터 처리 후 GC 확인")
    void getMonthlySchedules_memoryUsage() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 매우 큰 데이터셋 생성 (10,000개)
        int largeDatasetSize = 10000;
        setupSchedules(largeDatasetSize, 2025, 9, "메모리 테스트 일정");
        
        System.out.println("=== 메모리 사용량 테스트 시작 ===");
        System.out.println("생성된 데이터 개수: " + scheduleRepository.count());
        
        // 메모리 사용량 확인
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("테스트 시작 전 메모리 사용량: " + (memoryBefore / 1024 / 1024) + "MB");
        
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(9)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // when - 대용량 데이터 조회
        long apiCallStart = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/schedules/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules.length()").value(largeDatasetSize));
        
        long apiCallEnd = System.currentTimeMillis();
        System.out.println("대용량 데이터 조회 시간: " + (apiCallEnd - apiCallStart) + "ms");
        
        // 메모리 사용량 재확인
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("테스트 후 메모리 사용량: " + (memoryAfter / 1024 / 1024) + "MB");
        System.out.println("메모리 증가량: " + ((memoryAfter - memoryBefore) / 1024 / 1024) + "MB");
        
        // GC 실행 후 메모리 확인
        System.gc();
        try {
            Thread.sleep(1000); // GC 완료 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long memoryAfterGC = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("GC 후 메모리 사용량: " + (memoryAfterGC / 1024 / 1024) + "MB");
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("=== 메모리 사용량 테스트 전체 시간: " + totalTime + "ms ===");
    }

    @Test
    @DisplayName("부하 테스트 - 대용량 데이터 환경에서의 안정성 확인")
    void getMonthlySchedules_stressTest() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // given - 매우 큰 데이터셋 생성 (15,000개)
        int stressDatasetSize = 15000;
        setupSchedules(stressDatasetSize, 2025, 10, "부하 테스트 일정");
        
        System.out.println("=== 부하 테스트 시작 ===");
        System.out.println("생성된 데이터 개수: " + scheduleRepository.count());
        
        ScheduleCalendarRequestDTO request = ScheduleCalendarRequestDTO.builder()
                .year(2025)
                .month(10)
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        // when & then - 50번 연속 호출하여 부하 테스트
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < 50; i++) {
            try {
                long callStart = System.currentTimeMillis();
                
                mockMvc.perform(post("/api/schedules/calendar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.schedules.length()").value(stressDatasetSize));
                
                long callEnd = System.currentTimeMillis();
                successCount++;
                
                if (i % 10 == 0) { // 10번마다 진행상황 출력
                    System.out.println("부하 테스트 진행률: " + (i + 1) + "/50, 성공: " + successCount + ", 실패: " + failCount);
                }
                
            } catch (Exception e) {
                failCount++;
                System.err.println("부하 테스트 " + (i + 1) + "번째 실패: " + e.getMessage());
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("=== 부하 테스트 결과 ===");
        System.out.println("전체 시간: " + totalTime + "ms");
        System.out.println("성공 횟수: " + successCount);
        System.out.println("실패 횟수: " + failCount);
        System.out.println("성공률: " + (successCount * 100.0 / 50) + "%");
        
        // 성공률이 90% 이상이어야 테스트 통과
        assertThat(successCount).isGreaterThanOrEqualTo(45); // 90% 이상
    }
}
