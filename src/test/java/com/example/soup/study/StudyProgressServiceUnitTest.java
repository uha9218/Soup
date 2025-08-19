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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudyProgressServiceUnitTest {

    private StudyRepository studyRepository;
    private ScheduleRepository scheduleRepository;
    private SectionRepository sectionRepository;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private DeepStudyRepository deepStudyRepository;
    private StudyProgressService studyProgressService;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        scheduleRepository = mock(ScheduleRepository.class);
        sectionRepository = mock(SectionRepository.class);
        userRepository = mock(UserRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        deepStudyRepository = mock(DeepStudyRepository.class);
        
        studyProgressService = new StudyProgressService(
                studyRepository,
                scheduleRepository,
                sectionRepository,
                userRepository,
                reviewRepository,
                deepStudyRepository
        );
    }

    @Test
    @DisplayName("스터디 진행 현황 조회 성공")
    void getStudyProgress_success() {
        // given
        Long studyId = 1L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .build();

        // 스터디 mock
        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Spring Study");
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        // 스케줄 mock
        Schedule schedule1 = mock(Schedule.class);
        when(schedule1.getId()).thenReturn(10L);
        when(schedule1.getName()).thenReturn("Section 2~4");
        when(schedule1.getDescription()).thenReturn("스프링 기초");
        when(schedule1.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule1.getMeetingLocation()).thenReturn("온라인");
        when(schedule1.getHasDeepStudy()).thenReturn(true);

        Schedule schedule2 = mock(Schedule.class);
        when(schedule2.getId()).thenReturn(20L);
        when(schedule2.getName()).thenReturn("Section 5~7");
        when(schedule2.getDescription()).thenReturn("스프링 심화");
        when(schedule2.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 22, 20, 0));
        when(schedule2.getMeetingLocation()).thenReturn("강남역");
        when(schedule2.getHasDeepStudy()).thenReturn(false);

        when(scheduleRepository.findByStudyId(studyId))
                .thenReturn(Arrays.asList(schedule1, schedule2));

        // 섹션 mock
        Section section1 = mock(Section.class);
        when(section1.getId()).thenReturn(100L);
        when(section1.getSectionName()).thenReturn("Section 2");
        when(section1.getSectionNumber()).thenReturn(2L);
        when(section1.getNeedsReview()).thenReturn(true);
        when(section1.getSchedule()).thenReturn(schedule1);

        Section section2 = mock(Section.class);
        when(section2.getId()).thenReturn(200L);
        when(section2.getSectionName()).thenReturn("Section 3");
        when(section2.getSectionNumber()).thenReturn(3L);
        when(section2.getNeedsReview()).thenReturn(true);
        when(section2.getSchedule()).thenReturn(schedule1);

        Section section3 = mock(Section.class);
        when(section3.getId()).thenReturn(300L);
        when(section3.getSectionName()).thenReturn("Section 5");
        when(section3.getSectionNumber()).thenReturn(5L);
        when(section3.getNeedsReview()).thenReturn(false);
        when(section3.getSchedule()).thenReturn(schedule2);

        when(sectionRepository.findByScheduleId(10L))
                .thenReturn(Arrays.asList(section1, section2));
        when(sectionRepository.findByScheduleId(20L))
                .thenReturn(Arrays.asList(section3));

        // 사용자 mock
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1000L);
        when(user1.getUsername()).thenReturn("김철수");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2000L);
        when(user2.getUsername()).thenReturn("이영희");

        when(userRepository.findAll())
                .thenReturn(Arrays.asList(user1, user2));

        // 회고 mock
        Review review1 = mock(Review.class);
        when(review1.getUser()).thenReturn(user1);
        when(reviewRepository.findBySectionId(100L))
                .thenReturn(Arrays.asList(review1));
        when(reviewRepository.findBySectionId(200L))
                .thenReturn(Collections.emptyList());
        when(reviewRepository.findBySectionId(300L))
                .thenReturn(Collections.emptyList());

        // 심화학습 mock
        DeepStudy deepStudy1 = mock(DeepStudy.class);
        when(deepStudy1.getUser()).thenReturn(user2);
        when(deepStudyRepository.findByScheduleId(10L))
                .thenReturn(Arrays.asList(deepStudy1));
        when(deepStudyRepository.findByScheduleId(20L))
                .thenReturn(Collections.emptyList());

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(2);

        // 첫 번째 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO firstSchedule = result.getSchedules().get(0);
        assertThat(firstSchedule.getScheduleId()).isEqualTo(10L);
        assertThat(firstSchedule.getScheduleName()).isEqualTo("Section 2~4");
        assertThat(firstSchedule.getScheduleDate()).isEqualTo("2025-01-15");
        assertThat(firstSchedule.getScheduleTime()).isEqualTo("20:00");
        assertThat(firstSchedule.getDescription()).isEqualTo("스프링 기초");
        assertThat(firstSchedule.getMeetingLocation()).isEqualTo("온라인");
        assertThat(firstSchedule.getHasDeepStudy()).isTrue();
        assertThat(firstSchedule.getSections()).hasSize(2);

        // 두 번째 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO secondSchedule = result.getSchedules().get(1);
        assertThat(secondSchedule.getScheduleId()).isEqualTo(20L);
        assertThat(secondSchedule.getScheduleName()).isEqualTo("Section 5~7");
        assertThat(secondSchedule.getScheduleDate()).isEqualTo("2025-01-22");
        assertThat(secondSchedule.getScheduleTime()).isEqualTo("20:00");
        assertThat(secondSchedule.getDescription()).isEqualTo("스프링 심화");
        assertThat(secondSchedule.getMeetingLocation()).isEqualTo("강남역");
        assertThat(secondSchedule.getHasDeepStudy()).isFalse();
        assertThat(secondSchedule.getSections()).hasSize(1);

        // Repository 호출 검증
        verify(studyRepository).findById(studyId);
        verify(scheduleRepository).findByStudyId(studyId);
        verify(sectionRepository).findByScheduleId(10L);
        verify(sectionRepository).findByScheduleId(20L);
        verify(userRepository, times(3)).findAll(); // 섹션 3개에 대해 호출
        verify(reviewRepository, times(2)).findBySectionId(100L); // 섹션당 사용자 수만큼 호출 (2명)
        verify(reviewRepository, times(2)).findBySectionId(200L); // 섹션당 사용자 수만큼 호출 (2명)
        verify(reviewRepository, times(2)).findBySectionId(300L); // 섹션당 사용자 수만큼 호출 (2명)
        verify(deepStudyRepository, times(6)).findByScheduleId(anyLong()); // 섹션 3개 x 사용자 2명 = 6번 호출
    }

    @Test
    @DisplayName("스케줄이 없는 스터디인 경우 빈 스케줄 목록 반환")
    void getStudyProgress_emptySchedules() {
        // given
        Long studyId = 1L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .build();

        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Empty Study");
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(scheduleRepository.findByStudyId(studyId)).thenReturn(Collections.emptyList());

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Empty Study");
        assertThat(result.getSchedules()).isEmpty();

        verify(studyRepository).findById(studyId);
        verify(scheduleRepository).findByStudyId(studyId);
        verify(sectionRepository, never()).findByScheduleId(anyLong());
        verify(userRepository, never()).findAll();
        verify(reviewRepository, never()).findBySectionId(anyLong());
        verify(deepStudyRepository, never()).findByScheduleId(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 스터디 ID로 조회 시 예외 발생")
    void getStudyProgress_studyNotFound() {
        // given
        Long invalidStudyId = 999L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(invalidStudyId)
                .build();

        when(studyRepository.findById(invalidStudyId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyProgressService.getStudyProgress(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스터디가 존재하지 않습니다.");

        verify(studyRepository).findById(invalidStudyId);
        verify(scheduleRepository, never()).findByStudyId(anyLong());
        verify(sectionRepository, never()).findByScheduleId(anyLong());
        verify(userRepository, never()).findAll();
        verify(reviewRepository, never()).findBySectionId(anyLong());
        verify(deepStudyRepository, never()).findByScheduleId(anyLong());
    }

    @Test
    @DisplayName("섹션이 없는 스케줄인 경우 빈 섹션 목록 반환")
    void getStudyProgress_emptySections() {
        // given
        Long studyId = 1L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .build();

        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Test Study");
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(10L);
        when(schedule.getName()).thenReturn("Empty Schedule");
        when(schedule.getDescription()).thenReturn("빈 스케줄");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(false);

        when(scheduleRepository.findByStudyId(studyId)).thenReturn(Arrays.asList(schedule));
        when(sectionRepository.findByScheduleId(10L)).thenReturn(Collections.emptyList());

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Test Study");
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getSections()).isEmpty();

        verify(studyRepository).findById(studyId);
        verify(scheduleRepository).findByStudyId(studyId);
        verify(sectionRepository).findByScheduleId(10L);
        verify(userRepository, never()).findAll();
        verify(reviewRepository, never()).findBySectionId(anyLong());
        verify(deepStudyRepository, never()).findByScheduleId(anyLong());
    }

    @Test
    @DisplayName("회고와 심화학습 제출 상태 정확히 반영")
    void getStudyProgress_submissionStatus() {
        // given
        Long studyId = 1L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .build();

        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Test Study");
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(10L);
        when(schedule.getName()).thenReturn("Test Schedule");
        when(schedule.getDescription()).thenReturn("테스트");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(true);

        when(scheduleRepository.findByStudyId(studyId)).thenReturn(Arrays.asList(schedule));

        Section section = mock(Section.class);
        when(section.getId()).thenReturn(100L);
        when(section.getSectionName()).thenReturn("Test Section");
        when(section.getSectionNumber()).thenReturn(1L);
        when(section.getNeedsReview()).thenReturn(true);
        when(section.getSchedule()).thenReturn(schedule);

        when(sectionRepository.findByScheduleId(10L)).thenReturn(Arrays.asList(section));

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1000L);
        when(user1.getUsername()).thenReturn("김철수");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2000L);
        when(user2.getUsername()).thenReturn("이영희");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // user1은 회고 제출, user2는 심화학습 제출
        Review review = mock(Review.class);
        when(review.getUser()).thenReturn(user1);
        when(reviewRepository.findBySectionId(100L)).thenReturn(Arrays.asList(review));

        DeepStudy deepStudy = mock(DeepStudy.class);
        when(deepStudy.getUser()).thenReturn(user2);
        when(deepStudyRepository.findByScheduleId(10L)).thenReturn(Arrays.asList(deepStudy));

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        List<StudyProgressResponseDTO.MemberProgressDTO> members = 
                result.getSchedules().get(0).getSections().get(0).getMembers();

        // user1: 회고 제출O, 심화학습 제출X
        StudyProgressResponseDTO.MemberProgressDTO member1 = members.stream()
                .filter(m -> m.getUserId().equals(1000L))
                .findFirst()
                .orElseThrow();
        assertThat(member1.getReviewSubmitted()).isTrue();
        assertThat(member1.getDeepStudySubmitted()).isFalse();

        // user2: 회고 제출X, 심화학습 제출O
        StudyProgressResponseDTO.MemberProgressDTO member2 = members.stream()
                .filter(m -> m.getUserId().equals(2000L))
                .findFirst()
                .orElseThrow();
        assertThat(member2.getReviewSubmitted()).isFalse();
        assertThat(member2.getDeepStudySubmitted()).isTrue();
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 진행 현황 조회 성공")
    void getCurrentStudyProgress_success() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(null) // null로 설정하면 현재 진행 중인 스터디를 자동으로 찾음
                .build();

        // 현재 진행 중인 스터디 mock
        Study study = mock(Study.class);
        when(study.getId()).thenReturn(1L);
        when(study.getName()).thenReturn("Spring Study");
        when(studyRepository.findByIsActiveTrue()).thenReturn(Optional.of(study));

        // 스케줄 mock
        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(10L);
        when(schedule.getName()).thenReturn("Section 2~4");
        when(schedule.getDescription()).thenReturn("스프링 기초");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(true);

        when(scheduleRepository.findByStudyId(1L))
                .thenReturn(Collections.singletonList(schedule));

        // 섹션 mock
        Section section = mock(Section.class);
        when(section.getId()).thenReturn(100L);
        when(section.getSectionName()).thenReturn("Section 2");
        when(section.getSectionNumber()).thenReturn(2L);
        when(section.getNeedsReview()).thenReturn(true);
        when(section.getSchedule()).thenReturn(schedule);

        when(sectionRepository.findByScheduleId(10L))
                .thenReturn(Collections.singletonList(section));

        // 사용자 mock
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("김철수");

        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        // 회고 mock
        Review review = mock(Review.class);
        when(review.getUser()).thenReturn(user);

        when(reviewRepository.findBySectionId(100L))
                .thenReturn(Collections.singletonList(review));

        // 심화학습 mock
        DeepStudy deepStudy = mock(DeepStudy.class);
        when(deepStudy.getUser()).thenReturn(user);

        when(deepStudyRepository.findByScheduleId(10L))
                .thenReturn(Collections.singletonList(deepStudy));

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(1);

        StudyProgressResponseDTO.ScheduleProgressDTO scheduleProgress = result.getSchedules().get(0);
        assertThat(scheduleProgress.getScheduleId()).isEqualTo(10L);
        assertThat(scheduleProgress.getScheduleName()).isEqualTo("Section 2~4");
        assertThat(scheduleProgress.getHasDeepStudy()).isTrue();

        assertThat(scheduleProgress.getSections()).hasSize(1);
        StudyProgressResponseDTO.SectionProgressDTO sectionProgress = scheduleProgress.getSections().get(0);
        assertThat(sectionProgress.getSectionId()).isEqualTo(100L);
        assertThat(sectionProgress.getSectionName()).isEqualTo("Section 2");
        assertThat(sectionProgress.getNeedsReview()).isTrue();

        assertThat(sectionProgress.getMembers()).hasSize(1);
        StudyProgressResponseDTO.MemberProgressDTO memberProgress = sectionProgress.getMembers().get(0);
        assertThat(memberProgress.getUserId()).isEqualTo(1L);
        assertThat(memberProgress.getUserName()).isEqualTo("김철수");
        assertThat(memberProgress.getReviewSubmitted()).isTrue();
        assertThat(memberProgress.getDeepStudySubmitted()).isTrue();

        // verify
        verify(studyRepository).findByIsActiveTrue();
        verify(studyRepository, never()).findById(any());
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 진행 현황 조회 실패 - 진행 중인 스터디 없음")
    void getCurrentStudyProgress_fail_noActiveStudy() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(null)
                .build();

        when(studyRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyProgressService.getStudyProgress(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 진행 중인 스터디가 없습니다.");
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 조회 성공")
    void getCurrentActiveStudy_success() {
        // given
        Study study = mock(Study.class);
        when(studyRepository.findByIsActiveTrue()).thenReturn(Optional.of(study));

        // when
        Study result = studyProgressService.getCurrentActiveStudy();

        // then
        assertThat(result).isEqualTo(study);
        verify(studyRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 조회 실패 - 진행 중인 스터디 없음")
    void getCurrentActiveStudy_fail_noActiveStudy() {
        // given
        when(studyRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyProgressService.getCurrentActiveStudy())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 진행 중인 스터디가 없습니다.");
    }
}
