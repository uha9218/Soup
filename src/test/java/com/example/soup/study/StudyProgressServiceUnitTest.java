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
    private DeepStudyRepository deepStudyRepository;
    private UserRepository userRepository;
    private StudyProgressService studyProgressService;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        scheduleRepository = mock(ScheduleRepository.class);
        sectionRepository = mock(SectionRepository.class);
        deepStudyRepository = mock(DeepStudyRepository.class);
        userRepository = mock(UserRepository.class);
        studyProgressService = new StudyProgressService(
                studyRepository, 
                scheduleRepository, 
                sectionRepository, 
                deepStudyRepository, 
                userRepository
        );
    }

    @Test
    @DisplayName("2-Step Inquiry 패턴으로 스터디 진행 현황 조회 성공")
    void getStudyProgress_success() {
        // given
        Long studyId = 1L;
        Long scheduleId = 10L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .scheduleId(scheduleId)
                .build();

        // Step 1: 기본 엔티티들 mock
        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Spring Study");

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleId);
        when(schedule.getName()).thenReturn("Section 2~4");
        when(schedule.getDescription()).thenReturn("스프링 기초");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(true);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1000L);
        when(user1.getUsername()).thenReturn("김철수");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2000L);
        when(user2.getUsername()).thenReturn("이영희");

        // Step 2: 컬렉션 데이터들 mock
        Section section1 = mock(Section.class);
        when(section1.getId()).thenReturn(100L);
        when(section1.getSectionName()).thenReturn("Section 2");
        when(section1.getSectionNumber()).thenReturn(2L);
        when(section1.getNeedsReview()).thenReturn(true);

        Review review1 = mock(Review.class);
        when(review1.getUser()).thenReturn(user1);

        when(section1.getReviews()).thenReturn(Collections.singleton(review1));

        DeepStudy deepStudy1 = mock(DeepStudy.class);
        when(deepStudy1.getUser()).thenReturn(user2);

        // Repository mock 설정
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(sectionRepository.findByScheduleIdWithReviews(scheduleId)).thenReturn(Arrays.asList(section1));
        when(deepStudyRepository.findByScheduleIdWithUser(scheduleId)).thenReturn(Arrays.asList(deepStudy1));

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(1); // 2-Step Inquiry 패턴에서는 단일 스케줄만 반환
        
        // 스케줄 검증
        StudyProgressResponseDTO.ScheduleProgressDTO scheduleProgress = result.getSchedules().get(0);
        assertThat(scheduleProgress.getScheduleId()).isEqualTo(scheduleId);
        assertThat(scheduleProgress.getScheduleName()).isEqualTo("Section 2~4");
        assertThat(scheduleProgress.getScheduleDate()).isEqualTo("2025-01-15");
        assertThat(scheduleProgress.getScheduleTime()).isEqualTo("20:00");
        assertThat(scheduleProgress.getDescription()).isEqualTo("스프링 기초");
        assertThat(scheduleProgress.getMeetingLocation()).isEqualTo("온라인");
        assertThat(scheduleProgress.getHasDeepStudy()).isTrue();
        assertThat(scheduleProgress.getSections()).hasSize(1);

        // 섹션 검증
        StudyProgressResponseDTO.SectionProgressDTO section = scheduleProgress.getSections().get(0);
        assertThat(section.getSectionId()).isEqualTo(100L);
        assertThat(section.getSectionName()).isEqualTo("Section 2");
        assertThat(section.getSectionNumber()).isEqualTo(2L);
        assertThat(section.getNeedsReview()).isTrue();
        assertThat(section.getMembers()).hasSize(2);

        // 멤버 진행 현황 검증
        StudyProgressResponseDTO.MemberProgressDTO member1 = section.getMembers().stream()
                .filter(m -> m.getUserId().equals(1000L))
                .findFirst()
                .orElseThrow();
        assertThat(member1.getUserName()).isEqualTo("김철수");
        assertThat(member1.getReviewSubmitted()).isTrue(); // 리뷰 제출됨
        assertThat(member1.getDeepStudySubmitted()).isFalse(); // 심화학습 미제출

        StudyProgressResponseDTO.MemberProgressDTO member2 = section.getMembers().stream()
                .filter(m -> m.getUserId().equals(2000L))
                .findFirst()
                .orElseThrow();
        assertThat(member2.getUserName()).isEqualTo("이영희");
        assertThat(member2.getReviewSubmitted()).isFalse(); // 리뷰 미제출
        assertThat(member2.getDeepStudySubmitted()).isTrue(); // 심화학습 제출됨

        // Repository 호출 검증 - 2-Step Inquiry 패턴으로 여러 개의 최적화된 쿼리 호출
        verify(studyRepository).findById(studyId);
        verify(scheduleRepository).findById(scheduleId);
        verify(userRepository).findAll();
        verify(sectionRepository).findByScheduleIdWithReviews(scheduleId);
        verify(deepStudyRepository).findByScheduleIdWithUser(scheduleId);
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 ID로 조회 시 예외 발생")
    void getStudyProgress_scheduleNotFound() {
        // given
        Long studyId = 1L;
        Long scheduleId = 999L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .scheduleId(scheduleId)
                .build();

        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Test Study");
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyProgressService.getStudyProgress(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스케줄이 존재하지 않습니다.");

        verify(studyRepository).findById(studyId);
        verify(scheduleRepository).findById(scheduleId);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 ID로 조회 시 예외 발생")
    void getStudyProgress_studyNotFound() {
        // given
        Long invalidStudyId = 999L;
        Long scheduleId = 10L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(invalidStudyId)
                .scheduleId(scheduleId)
                .build();

        when(studyRepository.findById(invalidStudyId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyProgressService.getStudyProgress(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스터디가 존재하지 않습니다.");

        verify(studyRepository).findById(invalidStudyId);
    }

    @Test
    @DisplayName("빈 섹션과 심화학습이 있는 스케줄 조회")
    void getStudyProgress_emptySections() {
        // given
        Long studyId = 1L;
        Long scheduleId = 10L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(studyId)
                .scheduleId(scheduleId)
                .build();

        Study study = mock(Study.class);
        when(study.getId()).thenReturn(studyId);
        when(study.getName()).thenReturn("Test Study");

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleId);
        when(schedule.getName()).thenReturn("Empty Schedule");
        when(schedule.getDescription()).thenReturn("빈 스케줄");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(false);

        User user = mock(User.class);
        when(user.getId()).thenReturn(1000L);
        when(user.getUsername()).thenReturn("테스트유저");

        // Repository mock 설정
        when(studyRepository.findById(studyId)).thenReturn(Optional.of(study));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(sectionRepository.findByScheduleIdWithReviews(scheduleId)).thenReturn(Collections.emptyList());
        when(deepStudyRepository.findByScheduleIdWithUser(scheduleId)).thenReturn(Collections.emptyList());

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Test Study");
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getSections()).isEmpty();

        verify(studyRepository).findById(studyId);
        verify(scheduleRepository).findById(scheduleId);
        verify(userRepository).findAll();
        verify(sectionRepository).findByScheduleIdWithReviews(scheduleId);
        verify(deepStudyRepository).findByScheduleIdWithUser(scheduleId);
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