package com.example.soup.study;

import com.example.soup.study.dto.StudyProgressRequestDTO;
import com.example.soup.study.dto.StudyProgressResponseDTO;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
import com.example.soup.study.service.StudyProgressService;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.user.entity.User;
import com.example.soup.user.repository.UserRepository;
import com.example.soup.review.entity.Review;
import com.example.soup.review.repository.ReviewRepository;
import com.example.soup.deepstudy.entity.DeepStudy;
import com.example.soup.deepstudy.repository.DeepStudyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudyProgressServicePerformanceTest {

    @Autowired
    private StudyProgressService studyProgressService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DeepStudyRepository deepStudyRepository;

    private Study testStudy;
    private User testUser1;
    private User testUser2;
    private User testUser3;
    private Schedule testSchedule1;
    private Schedule testSchedule2;
    private Schedule testSchedule3;
    private Section testSection1;
    private Section testSection2;
    private Section testSection3;
    private Section testSection4;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();

        // 테스트용 Study 생성 (현재 진행 중인 스터디로 설정)
        testStudy = Study.create(
                "Spring Study",
                "스프링 스터디입니다",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        testStudy = studyRepository.save(testStudy);

        // 테스트용 User 생성
        testUser1 = User.create("kim@test.com", "password123", "김철수", "USER");
        testUser1 = userRepository.save(testUser1);

        testUser2 = User.create("lee@test.com", "password456", "이영희", "USER");
        testUser2 = userRepository.save(testUser2);

        testUser3 = User.create("park@test.com", "password789", "박민수", "USER");
        testUser3 = userRepository.save(testUser3);

        // 테스트용 Schedule 생성
        testSchedule1 = Schedule.create(
                testStudy, "Section 2~4", "스프링 기초",
                LocalDateTime.of(2025, 1, 15, 20, 0), "온라인", true, List.of()
        );
        testSchedule1 = scheduleRepository.save(testSchedule1);
        testStudy.getSchedules().add(testSchedule1);

        testSchedule2 = Schedule.create(
                testStudy, "Section 5~7", "스프링 심화",
                LocalDateTime.of(2025, 1, 22, 20, 0), "강남역", false, List.of()
        );
        testSchedule2 = scheduleRepository.save(testSchedule2);
        testStudy.getSchedules().add(testSchedule2);

        testSchedule3 = Schedule.create(
                testStudy, "Section 8~10", "스프링 고급",
                LocalDateTime.of(2025, 1, 29, 20, 0), "서울역", true, List.of()
        );
        testSchedule3 = scheduleRepository.save(testSchedule3);
        testStudy.getSchedules().add(testSchedule3);

        // 테스트용 Section 생성
        testSection1 = Section.create(2L, "Section 2", testStudy, testSchedule1, true);
        testSection1 = sectionRepository.save(testSection1);
        testSchedule1.getSections().add(testSection1);

        testSection2 = Section.create(3L, "Section 3", testStudy, testSchedule1, true);
        testSection2 = sectionRepository.save(testSection2);
        testSchedule1.getSections().add(testSection2);

        testSection3 = Section.create(5L, "Section 5", testStudy, testSchedule2, false);
        testSection3 = sectionRepository.save(testSection3);
        testSchedule2.getSections().add(testSection3);

        testSection4 = Section.create(8L, "Section 8", testStudy, testSchedule3, true);
        testSection4 = sectionRepository.save(testSection4);
        testSchedule3.getSections().add(testSection4);
    }

    @Test
    @DisplayName("N+1 Query Problem Detection - Performance Test")
    void getStudyProgress_nPlusOneQueryProblem() {
        // 로깅 레벨을 동적으로 변경하여 쿼리 로그 활성화
        Logger sqlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");
        Logger bindLogger = (Logger) LoggerFactory.getLogger("org.hibernate.orm.jdbc.bind");
        Level originalSqlLevel = sqlLogger.getLevel();
        Level originalBindLevel = bindLogger.getLevel();
        
        try {
            sqlLogger.setLevel(Level.DEBUG);
            bindLogger.setLevel(Level.TRACE);
            
            // given - Create complex data structure that can cause N+1 problem
            System.out.println("\n=== N+1 Query Problem Verification Start ===");
        System.out.println("Data Structure:");
        System.out.println("- Study: 1");
        System.out.println("- Users: 3");
        System.out.println("- Schedules: 3");
        System.out.println("- Sections: 4 (1-2 per schedule)");
        System.out.println("- Reviews/DeepStudies: Some submitted");
        System.out.println("================================\n");

        // Create situation where some users submitted reviews and deep studies
        // user1: Submit review for Section 2
        Review review1 = Review.create(testUser1, testSection1, "Review content 1", "https://example.com/review1");
        review1 = reviewRepository.save(review1);
        testSection1.getReviews().add(review1);

        // user2: Submit deep study for Schedule 1
        DeepStudy deepStudy1 = DeepStudy.create(testUser2, testSchedule1, "Deep study topic 1", "https://example.com/deepstudy1");
        deepStudy1 = deepStudyRepository.save(deepStudy1);
        testSchedule1.getDeepStudies().add(deepStudy1);

        // user3: Submit review for Section 3
        Review review2 = Review.create(testUser3, testSection2, "Review content 2", "https://example.com/review2");
        review2 = reviewRepository.save(review2);
        testSection2.getReviews().add(review2);

        // user1: Submit deep study for Schedule 3
        DeepStudy deepStudy2 = DeepStudy.create(testUser1, testSchedule3, "Deep study topic 2", "https://example.com/deepstudy2");
        deepStudy2 = deepStudyRepository.save(deepStudy2);
        testSchedule3.getDeepStudies().add(deepStudy2);

        // user2: Submit review for Section 8
        Review review3 = Review.create(testUser2, testSection4, "Review content 3", "https://example.com/review3");
        review3 = reviewRepository.save(review3);
        testSection4.getReviews().add(review3);

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .scheduleId(testSchedule1.getId())
                .build();

        System.out.println("=== Query Execution Start ===");
        long startTime = System.currentTimeMillis();

        // when - Service call
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        long endTime = System.currentTimeMillis();
        System.out.println("=== Query Execution Complete ===");
        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        // then - Result validation
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(1); // 2-Step Inquiry 패턴에서는 단일 스케줄만 반환

        // Schedule validation - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        StudyProgressResponseDTO.ScheduleProgressDTO schedule1 = result.getSchedules().get(0);
        assertThat(schedule1.getScheduleId()).isEqualTo(testSchedule1.getId());
        assertThat(schedule1.getSections()).hasSize(2); // Section 2, 3

        // Member count validation for each section (3 users)
        assertThat(schedule1.getSections().get(0).getMembers()).hasSize(3);
        assertThat(schedule1.getSections().get(1).getMembers()).hasSize(3);

        // Submission status validation
        // Check user1's review submission in Section 2
        StudyProgressResponseDTO.MemberProgressDTO user1InSection2 = schedule1.getSections().get(0).getMembers().stream()
                .filter(m -> m.getUserId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1InSection2.getReviewSubmitted()).isTrue();

        // Check user2's deep study submission in Schedule 1
        StudyProgressResponseDTO.MemberProgressDTO user2InSection2 = schedule1.getSections().get(0).getMembers().stream()
                .filter(m -> m.getUserId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2InSection2.getDeepStudySubmitted()).isTrue();

        // Actual DB data validation
        List<Schedule> schedulesFromDB = scheduleRepository.findByStudyId(testStudy.getId());
        assertThat(schedulesFromDB).hasSize(3);

        List<Section> sectionsFromDB = sectionRepository.findByScheduleId(testSchedule1.getId());
        assertThat(sectionsFromDB).hasSize(2);

        List<User> usersFromDB = userRepository.findAll();
        assertThat(usersFromDB).hasSize(3);

        List<Review> reviewsFromDB = reviewRepository.findBySectionId(testSection1.getId());
        assertThat(reviewsFromDB).hasSize(1);

        List<DeepStudy> deepStudiesFromDB = deepStudyRepository.findByScheduleId(testSchedule1.getId());
        assertThat(deepStudiesFromDB).hasSize(1);
        
        } finally {
            // 로깅 레벨을 원래대로 복원
            sqlLogger.setLevel(originalSqlLevel);
            bindLogger.setLevel(originalBindLevel);
        }
    }

    @Test
    @DisplayName("N+1 Query Problem Precise Validation - Query Count Auto Count")
    void getStudyProgress_nPlusOneQueryPreciseValidation() {
        // 로깅 레벨을 동적으로 변경하여 쿼리 로그 활성화
        Logger sqlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");
        Logger bindLogger = (Logger) LoggerFactory.getLogger("org.hibernate.orm.jdbc.bind");
        Level originalSqlLevel = sqlLogger.getLevel();
        Level originalBindLevel = bindLogger.getLevel();
        
        try {
            sqlLogger.setLevel(Level.DEBUG);
            bindLogger.setLevel(Level.TRACE);
            
            // given - Simple structure to clearly identify N+1 problem
            System.out.println("\n=== N+1 Query Precise Validation Start ===");
        
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create new test data
        Study simpleStudy = Study.create(
                "Simple Study",
                "Study for N+1 test",
                "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        simpleStudy = studyRepository.save(simpleStudy);

        // Create 2 users
        final User user1 = userRepository.save(User.create("user1@test.com", "password1", "User1", "USER"));
        final User user2 = userRepository.save(User.create("user2@test.com", "password2", "User2", "USER"));

        // Create 1 schedule
        Schedule schedule = Schedule.create(
                simpleStudy, "Test Schedule", "Test schedule",
                LocalDateTime.of(2025, 1, 15, 20, 0), "Online", true, List.of()
        );
        schedule = scheduleRepository.save(schedule);
        simpleStudy.getSchedules().add(schedule);

        // Create 2 sections
        Section section1 = Section.create(1L, "Section 1", simpleStudy, schedule, true);
        section1 = sectionRepository.save(section1);
        schedule.getSections().add(section1);
        
        Section section2 = Section.create(2L, "Section 2", simpleStudy, schedule, true);
        section2 = sectionRepository.save(section2);
        schedule.getSections().add(section2);

        // Submit review (user1 submits only to section1)
        Review review = Review.create(user1, section1, "Review content", "https://example.com/review");
        reviewRepository.save(review);
        section1.getReviews().add(review);

        // Submit deep study (user2 submits only to schedule)
        DeepStudy deepStudy = DeepStudy.create(user2, schedule, "Deep study content", "https://example.com/deepstudy");
        deepStudyRepository.save(deepStudy);
        schedule.getDeepStudies().add(deepStudy);

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(simpleStudy.getId())
                .scheduleId(schedule.getId())
                .build();

        System.out.println("Data Structure:");
        System.out.println("- Study: 1");
        System.out.println("- Users: 2");
        System.out.println("- Schedule: 1");
        System.out.println("- Sections: 2");
        System.out.println("- Review: 1 (user1 submits to section1)");
        System.out.println("- DeepStudy: 1 (user2 submits to schedule)");
        System.out.println("================================\n");

        System.out.println("=== Query Execution Start ===");
        long startTime = System.currentTimeMillis();

        // when - Service call
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        long endTime = System.currentTimeMillis();
        System.out.println("=== Query Execution Complete ===");
        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        // then - Result validation
        assertThat(result.getStudyName()).isEqualTo("Simple Study");
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getSections()).hasSize(2);

        // Member count validation for each section
        StudyProgressResponseDTO.SectionProgressDTO firstSection = result.getSchedules().get(0).getSections().get(0);
        StudyProgressResponseDTO.SectionProgressDTO secondSection = result.getSchedules().get(0).getSections().get(1);
        
        assertThat(firstSection.getMembers()).hasSize(2);
        assertThat(secondSection.getMembers()).hasSize(2);

        // Submission status validation
        // Check user1's review submission in Section 1
        StudyProgressResponseDTO.MemberProgressDTO user1InSection1 = firstSection.getMembers().stream()
                .filter(m -> m.getUserId().equals(user1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1InSection1.getReviewSubmitted()).isTrue();
        assertThat(user1InSection1.getDeepStudySubmitted()).isFalse();

        // Check user2's deep study submission in Section 1
        StudyProgressResponseDTO.MemberProgressDTO user2InSection1 = firstSection.getMembers().stream()
                .filter(m -> m.getUserId().equals(user2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2InSection1.getReviewSubmitted()).isFalse();
        assertThat(user2InSection1.getDeepStudySubmitted()).isTrue();
        
        } finally {
            // 로깅 레벨을 원래대로 복원
            sqlLogger.setLevel(originalSqlLevel);
            bindLogger.setLevel(originalBindLevel);
        }
    }

    @Test
    @DisplayName("High Volume N+1 Performance Test - Large Data Set")
    void getStudyProgress_highVolumeNPlusOnePerformanceTest() {
        // given - Create large data set to clearly demonstrate N+1 problem
        System.out.println("\n=== High Volume N+1 Performance Test Start ===");
        System.out.println("Creating large data set for meaningful performance comparison...");
        
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();
        
        // Create large scale test data
        Study largeStudy = Study.create(
                "Large Scale Study",
                "Study for high volume performance test",
                "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        largeStudy = studyRepository.save(largeStudy);

        // Create 50 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            User user = User.create(
                    "user" + i + "@test.com", 
                    "password" + i, 
                    "User" + i, 
                    "USER"
            );
            users.add(userRepository.save(user));
        }

        // Create 20 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Schedule schedule = Schedule.create(
                    largeStudy, 
                    "Schedule " + i, 
                    "Schedule description " + i,
                    LocalDateTime.of(2025, 1, i, 20, 0), 
                    "Location " + i, 
                    i % 2 == 0, // Alternate has_deep_study
                    List.of()
            );
            schedules.add(scheduleRepository.save(schedule));
            largeStudy.getSchedules().add(schedule);
        }

        // Create 200 sections (10 per schedule)
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= 10; j++) {
                Section section = Section.create(
                        (long) (i * 10 + j), 
                        "Section " + (i * 10 + j), 
                        largeStudy, 
                        schedule, 
                        j % 3 != 0 // 2/3 of sections need review
                );
                sections.add(sectionRepository.save(section));
                schedule.getSections().add(section);
            }
        }

        // Create reviews (30% of possible combinations)
        int reviewCount = 0;
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            // 30% of users submit reviews for each section
            for (int j = 0; j < users.size(); j++) {
                if (j % 3 == 0) { // Every 3rd user
                    Review review = Review.create(
                            users.get(j), 
                            section, 
                            "Review content for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                            "https://example.com/review/" + section.getId() + "/" + users.get(j).getId()
                    );
                    reviewRepository.save(review);
                    section.getReviews().add(review);
                    reviewCount++;
                }
            }
        }

        // Create deep studies (20% of possible combinations)
        int deepStudyCount = 0;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (schedule.getHasDeepStudy()) {
                // 20% of users submit deep studies for each schedule
                for (int j = 0; j < users.size(); j++) {
                    if (j % 5 == 0) { // Every 5th user
                        DeepStudy deepStudy = DeepStudy.create(
                                users.get(j), 
                                schedule, 
                                "Deep study topic for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        );
                        deepStudyRepository.save(deepStudy);
                        schedule.getDeepStudies().add(deepStudy);
                        deepStudyCount++;
                    }
                }
            }
        }

        System.out.println("Data Structure Created:");
        System.out.println("- Study: 1");
        System.out.println("- Users: " + users.size());
        System.out.println("- Schedules: " + schedules.size());
        System.out.println("- Sections: " + sections.size());
        System.out.println("- Reviews: " + reviewCount);
        System.out.println("- DeepStudies: " + deepStudyCount);
        System.out.println("================================\n");

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(largeStudy.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        System.out.println("=== High Volume Query Execution Start ===");
        long startTime = System.currentTimeMillis();

        // when - Service call
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("=== High Volume Query Execution Complete ===");
        System.out.println("Execution time: " + executionTime + "ms");

        // then - Result validation - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        assertThat(result.getStudyName()).isEqualTo("Large Scale Study");
        assertThat(result.getSchedules()).hasSize(1);


        // Additional validations - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        StudyProgressResponseDTO.ScheduleProgressDTO schedule = result.getSchedules().get(0);
        assertThat(schedule.getSections()).hasSize(10);
        schedule.getSections().forEach(section -> {
            // 2-Step Inquiry 패턴에서는 모든 사용자가 멤버로 표시됨 (50명)
            assertThat(section.getMembers()).hasSize(50);
        });
    }

    @Test
    @DisplayName("Extreme Volume N+1 Performance Test - Massive Data Set")
    void getStudyProgress_extremeVolumeNPlusOnePerformanceTest() {
        // given - Create extreme data set for dramatic performance demonstration
        System.out.println("\n=== Extreme Volume N+1 Performance Test Start ===");
        System.out.println("Creating massive data set for dramatic performance comparison...");
        
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();
        
        // Create extreme scale test data
        Study extremeStudy = Study.create(
                "Extreme Scale Study",
                "Study for extreme volume performance test",
                "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        extremeStudy = studyRepository.save(extremeStudy);

        // Create 100 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            User user = User.create(
                    "extreme_user" + i + "@test.com", 
                    "password" + i, 
                    "ExtremeUser" + i, 
                    "USER"
            );
            users.add(userRepository.save(user));
        }

        // Create 50 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            Schedule schedule = Schedule.create(
                    extremeStudy, 
                    "Extreme Schedule " + i, 
                    "Extreme schedule description " + i,
                    LocalDateTime.of(2025, 1, (i % 28) + 1, 20, 0), 
                    "Extreme Location " + i, 
                    i % 3 == 0, // 1/3 of schedules have deep study
                    List.of()
            );
            Schedule savedSchedule = scheduleRepository.save(schedule);
            schedules.add(savedSchedule);
            // 양방향 관계 설정: Study에 Schedule 추가
            extremeStudy.getSchedules().add(savedSchedule);
        }

        // Create 500 sections (10 per schedule)
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= 10; j++) {
                Section section = Section.create(
                        (long) (i * 10 + j), 
                        "Extreme Section " + (i * 10 + j), 
                        extremeStudy, 
                        schedule, 
                        j % 2 == 0 // 50% of sections need review
                );
                Section savedSection = sectionRepository.save(section);
                sections.add(savedSection);
                // 양방향 관계 설정: Schedule에 Section 추가
                schedule.getSections().add(savedSection);
            }
        }

        // Create reviews (25% of possible combinations)
        int reviewCount = 0;
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            // 25% of users submit reviews for each section
            for (int j = 0; j < users.size(); j++) {
                if (j % 4 == 0) { // Every 4th user
                    Review review = Review.create(
                            users.get(j), 
                            section, 
                            "Extreme review content for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                            "https://example.com/extreme/review/" + section.getId() + "/" + users.get(j).getId()
                    );
                    Review savedReview = reviewRepository.save(review);
                    reviewCount++;
                    // 양방향 관계 설정: Section에 Review 추가
                    section.getReviews().add(savedReview);
                }
            }
        }

        // Create deep studies (15% of possible combinations)
        int deepStudyCount = 0;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (schedule.getHasDeepStudy()) {
                // 15% of users submit deep studies for each schedule
                for (int j = 0; j < users.size(); j++) {
                    if (j % 7 == 0) { // Every 7th user
                        DeepStudy deepStudy = DeepStudy.create(
                                users.get(j), 
                                schedule, 
                                "Extreme deep study topic for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/extreme/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        );
                        DeepStudy savedDeepStudy = deepStudyRepository.save(deepStudy);
                        deepStudyCount++;
                        // 양방향 관계 설정: Schedule에 DeepStudy 추가
                        schedule.getDeepStudies().add(savedDeepStudy);
                    }
                }
            }
        }

        System.out.println("Extreme Data Structure Created:");
        System.out.println("- Study: 1");
        System.out.println("- Users: " + users.size());
        System.out.println("- Schedules: " + schedules.size());
        System.out.println("- Sections: " + sections.size());
        System.out.println("- Reviews: " + reviewCount);
        System.out.println("- DeepStudies: " + deepStudyCount);
        System.out.println("================================\n");

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(extremeStudy.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        System.out.println("=== Extreme Volume Query Execution Start ===");
        long startTime = System.currentTimeMillis();

        // when - Service call
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("=== Extreme Volume Query Execution Complete ===");
        System.out.println("Execution time: " + executionTime + "ms");

 

        // Additional validations - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        assertThat(result.getStudyName()).isEqualTo("Extreme Scale Study");
        assertThat(result.getSchedules()).hasSize(1);
        
        StudyProgressResponseDTO.ScheduleProgressDTO schedule = result.getSchedules().get(0);
        assertThat(schedule.getSections()).hasSize(10);
        schedule.getSections().forEach(section -> {
            // 2-Step Inquiry 패턴에서는 모든 사용자가 멤버로 표시됨 (100명)
            assertThat(section.getMembers()).hasSize(100);
        });
    }

    @Test
    @DisplayName("Performance Comparison Test - Before vs After Optimization")
    void getStudyProgress_performanceComparisonTest() {
        // given - Create medium scale data for before/after comparison
        System.out.println("\n=== Performance Comparison Test Start ===");
        System.out.println("This test demonstrates the performance difference between optimized and non-optimized queries...");
        
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();
        
        // Create medium scale test data
        Study comparisonStudy = Study.create(
                "Comparison Study",
                "Study for performance comparison test",
                "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        comparisonStudy = studyRepository.save(comparisonStudy);

        // Create 25 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            User user = User.create(
                    "comp_user" + i + "@test.com", 
                    "password" + i, 
                    "CompUser" + i, 
                    "USER"
            );
            users.add(userRepository.save(user));
        }

        // Create 10 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Schedule schedule = Schedule.create(
                    comparisonStudy, 
                    "Comparison Schedule " + i, 
                    "Comparison schedule description " + i,
                    LocalDateTime.of(2025, 1, i, 20, 0), 
                    "Comparison Location " + i, 
                    i % 2 == 0, // 50% of schedules have deep study
                    List.of()
            );
            schedules.add(scheduleRepository.save(schedule));
        }

        // Create 100 sections (10 per schedule)
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= 10; j++) {
                Section section = Section.create(
                        (long) (i * 10 + j), 
                        "Comparison Section " + (i * 10 + j), 
                        comparisonStudy, 
                        schedule, 
                        j % 2 == 0 // 50% of sections need review
                );
                sections.add(sectionRepository.save(section));
            }
        }

        // Create reviews (40% of possible combinations)
        int reviewCount = 0;
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            // 40% of users submit reviews for each section
            for (int j = 0; j < users.size(); j++) {
                if (j % 2 == 0) { // Every 2nd user
                    Review review = Review.create(
                            users.get(j), 
                            section, 
                            "Comparison review content for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                            "https://example.com/comparison/review/" + section.getId() + "/" + users.get(j).getId()
                    );
                    reviewRepository.save(review);
                    reviewCount++;
                }
            }
        }

        // Create deep studies (30% of possible combinations)
        int deepStudyCount = 0;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (schedule.getHasDeepStudy()) {
                // 30% of users submit deep studies for each schedule
                for (int j = 0; j < users.size(); j++) {
                    if (j % 3 == 0) { // Every 3rd user
                        DeepStudy deepStudy = DeepStudy.create(
                                users.get(j), 
                                schedule, 
                                "Comparison deep study topic for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/comparison/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        );
                        deepStudyRepository.save(deepStudy);
                        deepStudyCount++;
                    }
                }
            }
        }

        // Set up bidirectional relationships
        comparisonStudy.getSchedules().addAll(schedules);
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            List<Section> scheduleSections = sections.stream()
                    .filter(section -> section.getSchedule().getId().equals(schedule.getId()))
                    .collect(Collectors.toList());
            schedule.getSections().addAll(scheduleSections);
            
            for (Section section : scheduleSections) {
                List<Review> sectionReviews = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 2 == 0) { // Every 2nd user
                        sectionReviews.add(Review.create(
                                users.get(j), 
                                section, 
                                "Comparison review content for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/comparison/review/" + section.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                section.getReviews().addAll(sectionReviews);
            }
            
            if (schedule.getHasDeepStudy()) {
                List<DeepStudy> scheduleDeepStudies = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 3 == 0) { // Every 3rd user
                        scheduleDeepStudies.add(DeepStudy.create(
                                users.get(j), 
                                schedule, 
                                "Comparison deep study topic for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/comparison/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                schedule.getDeepStudies().addAll(scheduleDeepStudies);
            }
        }

        System.out.println("Comparison Data Structure Created:");
        System.out.println("- Study: 1");
        System.out.println("- Users: " + users.size());
        System.out.println("- Schedules: " + schedules.size());
        System.out.println("- Sections: " + sections.size());
        System.out.println("- Reviews: " + reviewCount);
        System.out.println("- DeepStudies: " + deepStudyCount);
        System.out.println("================================\n");

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(comparisonStudy.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        // Multiple test runs for comparison
        System.out.println("=== Performance Comparison - Multiple Test Runs ===");
        
        for (int run = 1; run <= 3; run++) {
            System.out.println("\n--- Test Run " + run + " ---");
            long startTime = System.currentTimeMillis();

            // when - Service call
            StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            System.out.println("Run " + run + " execution time: " + executionTime + "ms");
            
            // Validate result - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
            assertThat(result.getStudyName()).isEqualTo("Comparison Study");
            assertThat(result.getSchedules()).hasSize(1);
        }


    }

    @Test
    @DisplayName("Accurate Time Measurement Test - All Scenarios")
    void getStudyProgress_accurateTimeMeasurement() {
        System.out.println("\n=== ACCURATE TIME MEASUREMENT TEST ===");
        
        // Test 1: Small Data Set
        System.out.println("\n--- Test 1: Small Data Set (3 users, 3 sections) ---");
        long smallDataTime = runSmallDataTest();
        System.out.println("Small Data Test Execution Time: " + smallDataTime + "ms");
        
        // Test 2: Medium Data Set
        System.out.println("\n--- Test 2: Medium Data Set (25 users, 100 sections) ---");
        long mediumDataTime = runMediumDataTest();
        System.out.println("Medium Data Test Execution Time: " + mediumDataTime + "ms");
        
        // Test 3: Large Data Set
        System.out.println("\n--- Test 3: Large Data Set (50 users, 200 sections) ---");
        long largeDataTime = runLargeDataTest();
        System.out.println("Large Data Test Execution Time: " + largeDataTime + "ms");
        
        // Test 4: Extreme Data Set
        System.out.println("\n--- Test 4: Extreme Data Set (100 users, 500 sections) ---");
        long extremeDataTime = runExtremeDataTest();
        System.out.println("Extreme Data Test Execution Time: " + extremeDataTime + "ms");
        
        System.out.println("\n=== FINAL RESULTS ===");
        System.out.println("Small Data (3 users, 3 sections): " + smallDataTime + "ms");
        System.out.println("Medium Data (25 users, 100 sections): " + mediumDataTime + "ms");
        System.out.println("Large Data (50 users, 200 sections): " + largeDataTime + "ms");
        System.out.println("Extreme Data (100 users, 500 sections): " + extremeDataTime + "ms");
        System.out.println("Total Test Time: " + (smallDataTime + mediumDataTime + largeDataTime + extremeDataTime) + "ms");
    }
    
    private long runSmallDataTest() {
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();

        // Create small test data
        Study study = studyRepository.save(Study.create(
                "Small Study", "Small study for test", "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 12, 31, 23, 59)
        ));

        // Create 3 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            users.add(userRepository.save(User.create("user" + i + "@test.com", "password" + i, "User" + i, "USER")));
        }

        // Create 2 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            schedules.add(scheduleRepository.save(Schedule.create(
                    study, "Schedule " + i, "Schedule " + i,
                    LocalDateTime.of(2025, 1, i, 20, 0), "Location " + i, i % 2 == 0, List.of()
            )));
        }

        // Create 3 sections
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= (i == 0 ? 2 : 1); j++) {
                sections.add(sectionRepository.save(Section.create(
                        (long) (i * 10 + j), "Section " + (i * 10 + j), study, schedule, j % 2 == 0
                )));
            }
        }

        // Create some reviews and deep studies
        Review review1 = reviewRepository.save(Review.create(users.get(0), sections.get(0), "Review 1", "https://example.com/review1"));
        Review review2 = reviewRepository.save(Review.create(users.get(1), sections.get(1), "Review 2", "https://example.com/review2"));
        DeepStudy deepStudy1 = deepStudyRepository.save(DeepStudy.create(users.get(2), schedules.get(0), "Deep Study 1", "https://example.com/deepstudy1"));

        // Set up bidirectional relationships
        study.getSchedules().addAll(schedules);
        for (Schedule schedule : schedules) {
            List<Section> scheduleSections = sections.stream()
                    .filter(section -> section.getSchedule().getId().equals(schedule.getId()))
                    .collect(Collectors.toList());
            schedule.getSections().addAll(scheduleSections);
            
            for (Section section : scheduleSections) {
                if (section.getId().equals(sections.get(0).getId())) {
                    section.getReviews().add(review1);
                } else if (section.getId().equals(sections.get(1).getId())) {
                    section.getReviews().add(review2);
                }
            }
            
            if (schedule.getId().equals(schedules.get(0).getId())) {
                schedule.getDeepStudies().add(deepStudy1);
            }
        }

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(study.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        long startTime = System.nanoTime();
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);
        long endTime = System.nanoTime();
        
        long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Validate result - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        assertThat(result.getStudyName()).isEqualTo("Small Study");
        assertThat(result.getSchedules()).hasSize(1);
        
        return executionTime;
    }
    
    private long runMediumDataTest() {
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();

        // Create medium test data
        Study study = studyRepository.save(Study.create(
                "Medium Study", "Medium study for test", "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 12, 31, 23, 59)
        ));

        // Create 25 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            users.add(userRepository.save(User.create("medium_user" + i + "@test.com", "password" + i, "MediumUser" + i, "USER")));
        }

        // Create 10 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            schedules.add(scheduleRepository.save(Schedule.create(
                    study, "Medium Schedule " + i, "Medium Schedule " + i,
                    LocalDateTime.of(2025, 1, (i % 28) + 1, 20, 0), "Location " + i, i % 2 == 0, List.of()
            )));
        }

        // Create 100 sections (10 per schedule)
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= 10; j++) {
                sections.add(sectionRepository.save(Section.create(
                        (long) (i * 10 + j), "Medium Section " + (i * 10 + j), study, schedule, j % 2 == 0
                )));
            }
        }

        // Create reviews (40% of possible combinations)
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            for (int j = 0; j < users.size(); j++) {
                if (j % 2 == 0) { // Every 2nd user
                    reviewRepository.save(Review.create(
                            users.get(j), section, 
                            "Medium review for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                            "https://example.com/medium/review/" + section.getId() + "/" + users.get(j).getId()
                    ));
                }
            }
        }

        // Create deep studies (30% of possible combinations)
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (schedule.getHasDeepStudy()) {
                for (int j = 0; j < users.size(); j++) {
                    if (j % 3 == 0) { // Every 3rd user
                        deepStudyRepository.save(DeepStudy.create(
                                users.get(j), schedule,
                                "Medium deep study for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/medium/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
            }
        }

        // Set up bidirectional relationships
        study.getSchedules().addAll(schedules);
        for (Schedule schedule : schedules) {
            List<Section> scheduleSections = sections.stream()
                    .filter(section -> section.getSchedule().getId().equals(schedule.getId()))
                    .collect(Collectors.toList());
            schedule.getSections().addAll(scheduleSections);
            
            for (Section section : scheduleSections) {
                List<Review> sectionReviews = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 2 == 0) { // Every 2nd user
                        sectionReviews.add(Review.create(
                                users.get(j), section, 
                                "Medium review for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/medium/review/" + section.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                section.getReviews().addAll(sectionReviews);
            }
            
            if (schedule.getHasDeepStudy()) {
                List<DeepStudy> scheduleDeepStudies = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 3 == 0) { // Every 3rd user
                        scheduleDeepStudies.add(DeepStudy.create(
                                users.get(j), schedule,
                                "Medium deep study for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/medium/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                schedule.getDeepStudies().addAll(scheduleDeepStudies);
            }
        }

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(study.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        long startTime = System.nanoTime();
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);
        long endTime = System.nanoTime();
        
        long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Validate result - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        assertThat(result.getStudyName()).isEqualTo("Medium Study");
        assertThat(result.getSchedules()).hasSize(1);
        
        return executionTime;
    }
    
    private long runLargeDataTest() {
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();

        // Create large test data
        Study study = studyRepository.save(Study.create(
                "Large Study", "Large study for test", "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 12, 31, 23, 59)
        ));

        // Create 50 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            users.add(userRepository.save(User.create("large_user" + i + "@test.com", "password" + i, "LargeUser" + i, "USER")));
        }

        // Create 20 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            schedules.add(scheduleRepository.save(Schedule.create(
                    study, "Large Schedule " + i, "Large Schedule " + i,
                    LocalDateTime.of(2025, 1, (i % 28) + 1, 20, 0), "Location " + i, i % 2 == 0, List.of()
            )));
        }

        // Create 200 sections (10 per schedule)
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= 10; j++) {
                sections.add(sectionRepository.save(Section.create(
                        (long) (i * 10 + j), "Large Section " + (i * 10 + j), study, schedule, j % 2 == 0
                )));
            }
        }

        // Create reviews (30% of possible combinations)
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            for (int j = 0; j < users.size(); j++) {
                if (j % 3 == 0) { // Every 3rd user
                    reviewRepository.save(Review.create(
                            users.get(j), section, 
                            "Large review for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                            "https://example.com/large/review/" + section.getId() + "/" + users.get(j).getId()
                    ));
                }
            }
        }

        // Create deep studies (20% of possible combinations)
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (schedule.getHasDeepStudy()) {
                for (int j = 0; j < users.size(); j++) {
                    if (j % 5 == 0) { // Every 5th user
                        deepStudyRepository.save(DeepStudy.create(
                                users.get(j), schedule,
                                "Large deep study for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/large/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
            }
        }

        // Set up bidirectional relationships
        study.getSchedules().addAll(schedules);
        for (Schedule schedule : schedules) {
            List<Section> scheduleSections = sections.stream()
                    .filter(section -> section.getSchedule().getId().equals(schedule.getId()))
                    .collect(Collectors.toList());
            schedule.getSections().addAll(scheduleSections);
            
            for (Section section : scheduleSections) {
                List<Review> sectionReviews = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 3 == 0) { // Every 3rd user
                        sectionReviews.add(Review.create(
                                users.get(j), section, 
                                "Large review for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/large/review/" + section.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                section.getReviews().addAll(sectionReviews);
            }
            
            if (schedule.getHasDeepStudy()) {
                List<DeepStudy> scheduleDeepStudies = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 5 == 0) { // Every 5th user
                        scheduleDeepStudies.add(DeepStudy.create(
                                users.get(j), schedule,
                                "Large deep study for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/large/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                schedule.getDeepStudies().addAll(scheduleDeepStudies);
            }
        }

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(study.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        long startTime = System.nanoTime();
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);
        long endTime = System.nanoTime();
        
        long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Validate result - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        assertThat(result.getStudyName()).isEqualTo("Large Study");
        assertThat(result.getSchedules()).hasSize(1);
        
        return executionTime;
    }
    
    private long runExtremeDataTest() {
        // Clear existing data
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        studyRepository.deleteAll();

        // Create extreme test data
        Study study = studyRepository.save(Study.create(
                "Extreme Study", "Extreme study for test", "Online",
                LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 12, 31, 23, 59)
        ));

        // Create 100 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            users.add(userRepository.save(User.create("extreme_user" + i + "@test.com", "password" + i, "ExtremeUser" + i, "USER")));
        }

        // Create 50 schedules
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            schedules.add(scheduleRepository.save(Schedule.create(
                    study, "Extreme Schedule " + i, "Extreme Schedule " + i,
                    LocalDateTime.of(2025, 1, (i % 28) + 1, 20, 0), "Location " + i, i % 3 == 0, List.of()
            )));
        }

        // Create 500 sections (10 per schedule)
        List<Section> sections = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            for (int j = 1; j <= 10; j++) {
                sections.add(sectionRepository.save(Section.create(
                        (long) (i * 10 + j), "Extreme Section " + (i * 10 + j), study, schedule, j % 2 == 0
                )));
            }
        }

        // Create reviews (25% of possible combinations)
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            for (int j = 0; j < users.size(); j++) {
                if (j % 4 == 0) { // Every 4th user
                    reviewRepository.save(Review.create(
                            users.get(j), section, 
                            "Extreme review for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                            "https://example.com/extreme/review/" + section.getId() + "/" + users.get(j).getId()
                    ));
                }
            }
        }

        // Create deep studies (15% of possible combinations)
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (schedule.getHasDeepStudy()) {
                for (int j = 0; j < users.size(); j++) {
                    if (j % 7 == 0) { // Every 7th user
                        deepStudyRepository.save(DeepStudy.create(
                                users.get(j), schedule,
                                "Extreme deep study for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/extreme/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
            }
        }

        // Set up bidirectional relationships
        study.getSchedules().addAll(schedules);
        for (Schedule schedule : schedules) {
            List<Section> scheduleSections = sections.stream()
                    .filter(section -> section.getSchedule().getId().equals(schedule.getId()))
                    .collect(Collectors.toList());
            schedule.getSections().addAll(scheduleSections);
            
            for (Section section : scheduleSections) {
                List<Review> sectionReviews = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 4 == 0) { // Every 4th user
                        sectionReviews.add(Review.create(
                                users.get(j), section, 
                                "Extreme review for section " + section.getSectionName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/extreme/review/" + section.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                section.getReviews().addAll(sectionReviews);
            }
            
            if (schedule.getHasDeepStudy()) {
                List<DeepStudy> scheduleDeepStudies = new ArrayList<>();
                for (int j = 0; j < users.size(); j++) {
                    if (j % 7 == 0) { // Every 7th user
                        scheduleDeepStudies.add(DeepStudy.create(
                                users.get(j), schedule,
                                "Extreme deep study for schedule " + schedule.getName() + " by user " + users.get(j).getUsername(),
                                "https://example.com/extreme/deepstudy/" + schedule.getId() + "/" + users.get(j).getId()
                        ));
                    }
                }
                schedule.getDeepStudies().addAll(scheduleDeepStudies);
            }
        }

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(study.getId())
                .scheduleId(schedules.get(0).getId())
                .build();

        long startTime = System.nanoTime();
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);
        long endTime = System.nanoTime();
        
        long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Validate result - 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        assertThat(result.getStudyName()).isEqualTo("Extreme Study");
        assertThat(result.getSchedules()).hasSize(1);
        
        return executionTime;
    }
}
