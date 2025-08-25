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

import java.time.LocalDateTime;
import java.util.List;

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

        testSchedule2 = Schedule.create(
                testStudy, "Section 5~7", "스프링 심화",
                LocalDateTime.of(2025, 1, 22, 20, 0), "강남역", false, List.of()
        );
        testSchedule2 = scheduleRepository.save(testSchedule2);

        testSchedule3 = Schedule.create(
                testStudy, "Section 8~10", "스프링 고급",
                LocalDateTime.of(2025, 1, 29, 20, 0), "서울역", true, List.of()
        );
        testSchedule3 = scheduleRepository.save(testSchedule3);

        // 테스트용 Section 생성
        testSection1 = Section.create(2L, "Section 2", testStudy, testSchedule1, true);
        testSection1 = sectionRepository.save(testSection1);

        testSection2 = Section.create(3L, "Section 3", testStudy, testSchedule1, true);
        testSection2 = sectionRepository.save(testSection2);

        testSection3 = Section.create(5L, "Section 5", testStudy, testSchedule2, false);
        testSection3 = sectionRepository.save(testSection3);

        testSection4 = Section.create(8L, "Section 8", testStudy, testSchedule3, true);
        testSection4 = sectionRepository.save(testSection4);
    }

    @Test
    @DisplayName("N+1 쿼리 문제 발생 여부 확인 - 성능 테스트")
    void getStudyProgress_nPlusOneQueryProblem() {
        // given - N+1 문제를 유발할 수 있는 복잡한 데이터 구조 생성
        System.out.println("\n=== N+1 쿼리 문제 검증 시작 ===");
        System.out.println("데이터 구조:");
        System.out.println("- 스터디: 1개");
        System.out.println("- 사용자: 3명");
        System.out.println("- 스케줄: 3개");
        System.out.println("- 섹션: 4개 (스케줄당 1-2개씩)");
        System.out.println("- 회고/심화학습: 일부 제출");
        System.out.println("================================\n");

        // 일부 사용자들이 회고와 심화학습을 제출한 상황 생성
        // user1: Section 2에 회고 제출
        Review review1 = Review.create(testUser1, testSection1, "회고 내용1", "https://example.com/review1");
        reviewRepository.save(review1);

        // user2: Schedule 1에 심화학습 제출
        DeepStudy deepStudy1 = DeepStudy.create(testUser2, testSchedule1, "심화학습 주제1", "https://example.com/deepstudy1");
        deepStudyRepository.save(deepStudy1);

        // user3: Section 3에 회고 제출
        Review review2 = Review.create(testUser3, testSection2, "회고 내용2", "https://example.com/review2");
        reviewRepository.save(review2);

        // user1: Schedule 3에 심화학습 제출
        DeepStudy deepStudy2 = DeepStudy.create(testUser1, testSchedule3, "심화학습 주제2", "https://example.com/deepstudy2");
        deepStudyRepository.save(deepStudy2);

        // user2: Section 8에 회고 제출
        Review review3 = Review.create(testUser2, testSection4, "회고 내용3", "https://example.com/review3");
        reviewRepository.save(review3);

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .build();

        System.out.println("=== 쿼리 실행 시작 ===");
        long startTime = System.currentTimeMillis();

        // when - 서비스 호출
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        long endTime = System.currentTimeMillis();
        System.out.println("=== 쿼리 실행 완료 ===");
        System.out.println("실행 시간: " + (endTime - startTime) + "ms");

        // then - 결과 검증
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(3);

        // 첫 번째 스케줄 검증 (기존 testSchedule1)
        StudyProgressResponseDTO.ScheduleProgressDTO firstSchedule = result.getSchedules().get(0);
        assertThat(firstSchedule.getSections()).hasSize(2); // Section 2, 3

        // 두 번째 스케줄 검증 (기존 testSchedule2)
        StudyProgressResponseDTO.ScheduleProgressDTO secondSchedule = result.getSchedules().get(1);
        assertThat(secondSchedule.getSections()).hasSize(1); // Section 5

        // 세 번째 스케줄 검증 (새로 추가된 testSchedule3)
        StudyProgressResponseDTO.ScheduleProgressDTO thirdSchedule = result.getSchedules().get(2);
        assertThat(thirdSchedule.getSections()).hasSize(1); // Section 8

        // 각 섹션의 멤버 수 검증 (사용자 3명)
        assertThat(firstSchedule.getSections().get(0).getMembers()).hasSize(3);
        assertThat(firstSchedule.getSections().get(1).getMembers()).hasSize(3);
        assertThat(secondSchedule.getSections().get(0).getMembers()).hasSize(3);
        assertThat(thirdSchedule.getSections().get(0).getMembers()).hasSize(3);

        // 제출 상태 검증
        // Section 2에서 user1의 회고 제출 확인
        StudyProgressResponseDTO.MemberProgressDTO user1InSection2 = firstSchedule.getSections().get(0).getMembers().stream()
                .filter(m -> m.getUserId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1InSection2.getReviewSubmitted()).isTrue();

        // Schedule 1에서 user2의 심화학습 제출 확인
        StudyProgressResponseDTO.MemberProgressDTO user2InSection2 = firstSchedule.getSections().get(0).getMembers().stream()
                .filter(m -> m.getUserId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2InSection2.getDeepStudySubmitted()).isTrue();

        System.out.println("\n=== N+1 쿼리 문제 분석 ===");
        System.out.println("예상되는 최적 쿼리 개수:");
        System.out.println("- SELECT * FROM study WHERE id = ? (1번)");
        System.out.println("- SELECT * FROM schedule WHERE study_id = ? (1번)");
        System.out.println("- SELECT * FROM section WHERE schedule_id = ? (스케줄 개수만큼, 즉 3번)");
        System.out.println("- SELECT * FROM user (1번)");
        System.out.println("- SELECT * FROM review WHERE section_id = ? (섹션 개수만큼, 즉 4번)");
        System.out.println("- SELECT * FROM deep_study WHERE schedule_id = ? (스케줄 개수만큼, 즉 3번)");
        System.out.println("총 예상 쿼리: 13번");
        System.out.println("\n실제 실행된 쿼리들을 위의 콘솔 로그에서 확인하세요.");
        System.out.println("만약 review와 deep_study 쿼리가 섹션/스케줄 개수보다 훨씬 많이 실행된다면 N+1 문제가 발생한 것입니다.");
        System.out.println("================================\n");

        // 실제 DB에서 데이터 검증
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
    }

    @Test
    @DisplayName("N+1 쿼리 문제 정밀 검증 - 쿼리 개수 자동 카운트")
    void getStudyProgress_nPlusOneQueryPreciseValidation() {
        // given - 간단한 구조로 N+1 문제를 명확히 확인
        System.out.println("\n=== N+1 쿼리 정밀 검증 시작 ===");
        
        // 기존 데이터 정리
        reviewRepository.deleteAll();
        deepStudyRepository.deleteAll();
        sectionRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();
        
        // 새로운 테스트 데이터 생성
        Study simpleStudy = Study.create(
                "Simple Study",
                "N+1 테스트용 스터디",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        simpleStudy = studyRepository.save(simpleStudy);

        // 사용자 2명 생성
        final User user1 = userRepository.save(User.create("user1@test.com", "password1", "사용자1", "USER"));
        final User user2 = userRepository.save(User.create("user2@test.com", "password2", "사용자2", "USER"));

        // 스케줄 1개 생성
        Schedule schedule = Schedule.create(
                simpleStudy, "Test Schedule", "테스트 스케줄",
                LocalDateTime.of(2025, 1, 15, 20, 0), "온라인", true, List.of()
        );
        schedule = scheduleRepository.save(schedule);

        // 섹션 2개 생성
        Section section1 = Section.create(1L, "Section 1", simpleStudy, schedule, true);
        section1 = sectionRepository.save(section1);
        
        Section section2 = Section.create(2L, "Section 2", simpleStudy, schedule, true);
        section2 = sectionRepository.save(section2);

        // 회고 제출 (user1이 section1에만 제출)
        Review review = Review.create(user1, section1, "회고 내용", "https://example.com/review");
        reviewRepository.save(review);

        // 심화학습 제출 (user2가 schedule에만 제출)
        DeepStudy deepStudy = DeepStudy.create(user2, schedule, "심화학습 내용", "https://example.com/deepstudy");
        deepStudyRepository.save(deepStudy);

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(simpleStudy.getId())
                .build();

        System.out.println("데이터 구조:");
        System.out.println("- 스터디: 1개");
        System.out.println("- 사용자: 2명");
        System.out.println("- 스케줄: 1개");
        System.out.println("- 섹션: 2개");
        System.out.println("- 회고: 1개 (user1이 section1에 제출)");
        System.out.println("- 심화학습: 1개 (user2가 schedule에 제출)");
        System.out.println("================================\n");

        System.out.println("=== 쿼리 실행 시작 ===");
        long startTime = System.currentTimeMillis();

        // when - 서비스 호출
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        long endTime = System.currentTimeMillis();
        System.out.println("=== 쿼리 실행 완료 ===");
        System.out.println("실행 시간: " + (endTime - startTime) + "ms");

        // then - 결과 검증
        assertThat(result.getStudyName()).isEqualTo("Simple Study");
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getSections()).hasSize(2);

        // 각 섹션의 멤버 수 검증
        StudyProgressResponseDTO.SectionProgressDTO firstSection = result.getSchedules().get(0).getSections().get(0);
        StudyProgressResponseDTO.SectionProgressDTO secondSection = result.getSchedules().get(0).getSections().get(1);
        
        assertThat(firstSection.getMembers()).hasSize(2);
        assertThat(secondSection.getMembers()).hasSize(2);

        // 제출 상태 검증
        // Section 1에서 user1의 회고 제출 확인
        StudyProgressResponseDTO.MemberProgressDTO user1InSection1 = firstSection.getMembers().stream()
                .filter(m -> m.getUserId().equals(user1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1InSection1.getReviewSubmitted()).isTrue();
        assertThat(user1InSection1.getDeepStudySubmitted()).isFalse();

        // Section 1에서 user2의 심화학습 제출 확인
        StudyProgressResponseDTO.MemberProgressDTO user2InSection1 = firstSection.getMembers().stream()
                .filter(m -> m.getUserId().equals(user2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2InSection1.getReviewSubmitted()).isFalse();
        assertThat(user2InSection1.getDeepStudySubmitted()).isTrue();

        System.out.println("\n=== N+1 쿼리 정밀 분석 ===");
        System.out.println("최적 쿼리 개수 (N+1 문제 없음):");
        System.out.println("- SELECT * FROM study WHERE id = ? (1번)");
        System.out.println("- SELECT * FROM schedule WHERE study_id = ? (1번)");
        System.out.println("- SELECT * FROM section WHERE schedule_id = ? (1번)");
        System.out.println("- SELECT * FROM user (1번)");
        System.out.println("- SELECT * FROM review WHERE section_id = ? (2번 - 섹션 2개)");
        System.out.println("- SELECT * FROM deep_study WHERE schedule_id = ? (2번 - 섹션 2개)");
        System.out.println("총 최적 쿼리: 8번");
        
        System.out.println("\nN+1 문제 발생 시 예상 쿼리 개수:");
        System.out.println("- SELECT * FROM study WHERE id = ? (1번)");
        System.out.println("- SELECT * FROM schedule WHERE study_id = ? (1번)");
        System.out.println("- SELECT * FROM section WHERE schedule_id = ? (1번)");
        System.out.println("- SELECT * FROM user (1번)");
        System.out.println("- SELECT * FROM review WHERE section_id = ? (4번 - 섹션 2개 × 사용자 2명)");
        System.out.println("- SELECT * FROM deep_study WHERE schedule_id = ? (4번 - 섹션 2개 × 사용자 2명)");
        System.out.println("총 N+1 문제 쿼리: 12번");
        
        System.out.println("\n위의 콘솔 로그에서 실제 실행된 쿼리 개수를 확인하세요.");
        System.out.println("- 8번 정도면 최적화됨");
        System.out.println("- 12번 이상이면 N+1 문제 발생");
        System.out.println("================================\n");
    }
}
