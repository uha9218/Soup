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
class StudyProgressServiceIntegrationTest {

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
    private Schedule testSchedule1;
    private Schedule testSchedule2;
    private Section testSection1;
    private Section testSection2;
    private Section testSection3;

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

        // 테스트용 Section 생성
        testSection1 = Section.create(2L, "Section 2", testStudy, testSchedule1, true);
        testSection1 = sectionRepository.save(testSection1);

        testSection2 = Section.create(3L, "Section 3", testStudy, testSchedule1, true);
        testSection2 = sectionRepository.save(testSection2);

        testSection3 = Section.create(5L, "Section 5", testStudy, testSchedule2, false);
        testSection3 = sectionRepository.save(testSection3);
    }

    @Test
    @DisplayName("스터디 진행 현황 조회 성공 - 통합 테스트")
    void getStudyProgress_success() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .build();

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(2);

        // 첫 번째 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO firstSchedule = result.getSchedules().get(0);
        assertThat(firstSchedule.getScheduleId()).isEqualTo(testSchedule1.getId());
        assertThat(firstSchedule.getScheduleName()).isEqualTo("Section 2~4");
        assertThat(firstSchedule.getScheduleDate()).isEqualTo("2025-01-15");
        assertThat(firstSchedule.getScheduleTime()).isEqualTo("20:00");
        assertThat(firstSchedule.getDescription()).isEqualTo("스프링 기초");
        assertThat(firstSchedule.getMeetingLocation()).isEqualTo("온라인");
        assertThat(firstSchedule.getHasDeepStudy()).isTrue();
        assertThat(firstSchedule.getSections()).hasSize(2);

        // 두 번째 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO secondSchedule = result.getSchedules().get(1);
        assertThat(secondSchedule.getScheduleId()).isEqualTo(testSchedule2.getId());
        assertThat(secondSchedule.getScheduleName()).isEqualTo("Section 5~7");
        assertThat(secondSchedule.getScheduleDate()).isEqualTo("2025-01-22");
        assertThat(secondSchedule.getScheduleTime()).isEqualTo("20:00");
        assertThat(secondSchedule.getDescription()).isEqualTo("스프링 심화");
        assertThat(secondSchedule.getMeetingLocation()).isEqualTo("강남역");
        assertThat(secondSchedule.getHasDeepStudy()).isFalse();
        assertThat(secondSchedule.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("회고와 심화학습 제출 상태 정확히 반영 - 통합 테스트")
    void getStudyProgress_submissionStatus() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .build();

        // user1이 Section 2에 회고 제출
        Review review1 = Review.create(testUser1, testSection1, "회고 내용", "https://example.com/review1");
        reviewRepository.save(review1);

        // user2가 Schedule 1에 심화학습 제출
        DeepStudy deepStudy1 = DeepStudy.create(testUser2, testSchedule1, "심화학습 주제", "https://example.com/deepstudy1");
        deepStudyRepository.save(deepStudy1);

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        List<StudyProgressResponseDTO.MemberProgressDTO> members = 
                result.getSchedules().get(0).getSections().get(0).getMembers();

        // user1: 회고 제출O, 심화학습 제출X
        StudyProgressResponseDTO.MemberProgressDTO member1 = members.stream()
                .filter(m -> m.getUserId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(member1.getReviewSubmitted()).isTrue();
        assertThat(member1.getDeepStudySubmitted()).isFalse();

        // user2: 회고 제출X, 심화학습 제출O
        StudyProgressResponseDTO.MemberProgressDTO member2 = members.stream()
                .filter(m -> m.getUserId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(member2.getReviewSubmitted()).isFalse();
        assertThat(member2.getDeepStudySubmitted()).isTrue();
    }

    @Test
    @DisplayName("스케줄이 없는 스터디인 경우 빈 스케줄 목록 반환 - 통합 테스트")
    void getStudyProgress_emptySchedules() {
        // given
        Study emptyStudy = Study.create(
                "Empty Study",
                "빈 스터디입니다",
                "온라인",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        emptyStudy = studyRepository.save(emptyStudy);

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(emptyStudy.getId())
                .build();

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Empty Study");
        assertThat(result.getSchedules()).isEmpty();
    }

    @Test
    @DisplayName("섹션이 없는 스케줄인 경우 빈 섹션 목록 반환 - 통합 테스트")
    void getStudyProgress_emptySections() {
        // given
        final Schedule emptySchedule = Schedule.create(
                testStudy, "Empty Schedule", "빈 스케줄",
                LocalDateTime.of(2025, 2, 1, 20, 0), "온라인", false, List.of()
        );
        final Schedule savedEmptySchedule = scheduleRepository.save(emptySchedule);

        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .build();

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getSchedules()).hasSize(3); // 기존 2개 + 새로 추가된 1개
        StudyProgressResponseDTO.ScheduleProgressDTO emptyScheduleDTO = result.getSchedules().stream()
                .filter(s -> s.getScheduleId().equals(savedEmptySchedule.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(emptyScheduleDTO.getSections()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 스터디 ID로 조회 시 예외 발생 - 통합 테스트")
    void getStudyProgress_studyNotFound() {
        // given
        Long invalidStudyId = 999L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(invalidStudyId)
                .build();

        // when & then
        assertThatThrownBy(() -> studyProgressService.getStudyProgress(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스터디가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("여러 사용자의 회고와 심화학습 제출 상태 복합 검증 - 통합 테스트")
    void getStudyProgress_complexSubmissionStatus() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .build();

        // user1: Section 2에만 회고 제출
        Review review1 = Review.create(testUser1, testSection1, "회고 내용1", "https://example.com/review1");
        reviewRepository.save(review1);

        // user2: Schedule 1에만 심화학습 제출
        DeepStudy deepStudy1 = DeepStudy.create(testUser2, testSchedule1, "심화학습 주제1", "https://example.com/deepstudy1");
        deepStudyRepository.save(deepStudy1);

        // user1: Section 3에도 회고 제출
        Review review2 = Review.create(testUser1, testSection2, "회고 내용2", "https://example.com/review2");
        reviewRepository.save(review2);

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        // Section 2 (첫 번째 섹션) 검증
        List<StudyProgressResponseDTO.MemberProgressDTO> section2Members = 
                result.getSchedules().get(0).getSections().get(0).getMembers();

        StudyProgressResponseDTO.MemberProgressDTO user1InSection2 = section2Members.stream()
                .filter(m -> m.getUserId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1InSection2.getReviewSubmitted()).isTrue(); // Section 2에 회고 제출
        assertThat(user1InSection2.getDeepStudySubmitted()).isFalse(); // Schedule 1에 심화학습 제출 안함

        StudyProgressResponseDTO.MemberProgressDTO user2InSection2 = section2Members.stream()
                .filter(m -> m.getUserId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2InSection2.getReviewSubmitted()).isFalse(); // Section 2에 회고 제출 안함
        assertThat(user2InSection2.getDeepStudySubmitted()).isTrue(); // Schedule 1에 심화학습 제출

        // Section 3 (두 번째 섹션) 검증
        List<StudyProgressResponseDTO.MemberProgressDTO> section3Members = 
                result.getSchedules().get(0).getSections().get(1).getMembers();

        StudyProgressResponseDTO.MemberProgressDTO user1InSection3 = section3Members.stream()
                .filter(m -> m.getUserId().equals(testUser1.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user1InSection3.getReviewSubmitted()).isTrue(); // Section 3에 회고 제출
        assertThat(user1InSection3.getDeepStudySubmitted()).isFalse(); // Schedule 1에 심화학습 제출 안함

        StudyProgressResponseDTO.MemberProgressDTO user2InSection3 = section3Members.stream()
                .filter(m -> m.getUserId().equals(testUser2.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(user2InSection3.getReviewSubmitted()).isFalse(); // Section 3에 회고 제출 안함
        assertThat(user2InSection3.getDeepStudySubmitted()).isTrue(); // Schedule 1에 심화학습 제출
    }

    @Test
    @DisplayName("Repository 쿼리 검증 - 통합 테스트")
    void verifyRepositoryQuery() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(testStudy.getId())
                .build();

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(2);

        // 실제 DB에서 조회된 데이터 검증
        List<Schedule> schedulesFromDB = scheduleRepository.findByStudyId(testStudy.getId());
        assertThat(schedulesFromDB).hasSize(2);

        List<Section> sectionsFromDB = sectionRepository.findByScheduleId(testSchedule1.getId());
        assertThat(sectionsFromDB).hasSize(2);

        List<User> usersFromDB = userRepository.findAll();
        assertThat(usersFromDB).hasSize(2);
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 진행 현황 조회 성공 - 통합 테스트")
    void getCurrentStudyProgress_success() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(null) // null로 설정하면 현재 진행 중인 스터디를 자동으로 찾음
                .build();

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(2);

        // 첫 번째 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO firstSchedule = result.getSchedules().get(0);
        assertThat(firstSchedule.getScheduleId()).isEqualTo(testSchedule1.getId());
        assertThat(firstSchedule.getScheduleName()).isEqualTo("Section 2~4");
        assertThat(firstSchedule.getHasDeepStudy()).isTrue();

        // 두 번째 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO secondSchedule = result.getSchedules().get(1);
        assertThat(secondSchedule.getScheduleId()).isEqualTo(testSchedule2.getId());
        assertThat(secondSchedule.getScheduleName()).isEqualTo("Section 5~7");
        assertThat(secondSchedule.getHasDeepStudy()).isFalse();
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 조회 성공 - 통합 테스트")
    void getCurrentActiveStudy_success() {
        // when
        Study result = studyProgressService.getCurrentActiveStudy();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Spring Study");
        assertThat(result.getIsActive()).isTrue();
    }
}
