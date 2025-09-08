package com.example.soup.study;

import com.example.soup.study.dto.StudyProgressRequestDTO;
import com.example.soup.study.dto.StudyProgressResponseDTO;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
import com.example.soup.study.service.StudyProgressService;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.section.entity.Section;
import com.example.soup.user.entity.User;
import com.example.soup.review.entity.Review;
import com.example.soup.deepstudy.entity.DeepStudy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudyProgressServiceUnitTest {

    private StudyRepository studyRepository;
    private StudyProgressService studyProgressService;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        studyProgressService = new StudyProgressService(studyRepository);
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

        // 사용자 mock
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1000L);
        when(user1.getUsername()).thenReturn("김철수");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2000L);
        when(user2.getUsername()).thenReturn("이영희");

        // 회고 mock
        Review review1 = mock(Review.class);
        when(review1.getUser()).thenReturn(user1);
        when(review1.getSection()).thenReturn(section1);

        // 심화학습 mock
        DeepStudy deepStudy1 = mock(DeepStudy.class);
        when(deepStudy1.getUser()).thenReturn(user2);
        when(deepStudy1.getSchedule()).thenReturn(schedule1);

        // Fetch Join으로 로드된 데이터 설정
        when(study.getSchedules()).thenReturn(new HashSet<>(Arrays.asList(schedule1, schedule2)));
        when(schedule1.getSections()).thenReturn(Arrays.asList(section1, section2));
        when(schedule2.getSections()).thenReturn(Arrays.asList(section3));
        when(section1.getReviews()).thenReturn(new HashSet<>(Arrays.asList(review1)));
        when(section2.getReviews()).thenReturn(Collections.emptySet());
        when(section3.getReviews()).thenReturn(Collections.emptySet());
        when(schedule1.getDeepStudies()).thenReturn(new HashSet<>(Arrays.asList(deepStudy1)));
        when(schedule2.getDeepStudies()).thenReturn(Collections.emptySet());

        when(studyRepository.findByIdWithDetails(studyId)).thenReturn(Optional.of(study));

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Spring Study");
        assertThat(result.getSchedules()).hasSize(2);
        
        // 스케줄 ID로 찾아서 검증
        StudyProgressResponseDTO.ScheduleProgressDTO firstSchedule = result.getSchedules().stream()
                .filter(s -> s.getScheduleId().equals(10L))
                .findFirst()
                .orElseThrow();
        assertThat(firstSchedule.getScheduleName()).isEqualTo("Section 2~4");
        assertThat(firstSchedule.getScheduleDate()).isEqualTo("2025-01-15");
        assertThat(firstSchedule.getScheduleTime()).isEqualTo("20:00");
        assertThat(firstSchedule.getDescription()).isEqualTo("스프링 기초");
        assertThat(firstSchedule.getMeetingLocation()).isEqualTo("온라인");
        assertThat(firstSchedule.getHasDeepStudy()).isTrue();
        assertThat(firstSchedule.getSections()).hasSize(2);

        StudyProgressResponseDTO.ScheduleProgressDTO secondSchedule = result.getSchedules().stream()
                .filter(s -> s.getScheduleId().equals(20L))
                .findFirst()
                .orElseThrow();
        assertThat(secondSchedule.getScheduleName()).isEqualTo("Section 5~7");
        assertThat(secondSchedule.getScheduleDate()).isEqualTo("2025-01-22");
        assertThat(secondSchedule.getScheduleTime()).isEqualTo("20:00");
        assertThat(secondSchedule.getDescription()).isEqualTo("스프링 심화");
        assertThat(secondSchedule.getMeetingLocation()).isEqualTo("강남역");
        assertThat(secondSchedule.getHasDeepStudy()).isFalse();
        assertThat(secondSchedule.getSections()).hasSize(1);

        // Repository 호출 검증 - Fetch Join으로 단일 호출만 발생
        verify(studyRepository).findByIdWithDetails(studyId);
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
        when(study.getSchedules()).thenReturn(Collections.emptySet());
        when(studyRepository.findByIdWithDetails(studyId)).thenReturn(Optional.of(study));

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Empty Study");
        assertThat(result.getSchedules()).isEmpty();

        verify(studyRepository).findByIdWithDetails(studyId);
    }

    @Test
    @DisplayName("존재하지 않는 스터디 ID로 조회 시 예외 발생")
    void getStudyProgress_studyNotFound() {
        // given
        Long invalidStudyId = 999L;
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(invalidStudyId)
                .build();

        when(studyRepository.findByIdWithDetails(invalidStudyId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyProgressService.getStudyProgress(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 스터디가 존재하지 않습니다.");

        verify(studyRepository).findByIdWithDetails(invalidStudyId);
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

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(10L);
        when(schedule.getName()).thenReturn("Empty Schedule");
        when(schedule.getDescription()).thenReturn("빈 스케줄");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(false);
        when(schedule.getSections()).thenReturn(Collections.emptyList());
        when(schedule.getDeepStudies()).thenReturn(Collections.emptySet());

        when(study.getSchedules()).thenReturn(new HashSet<>(Arrays.asList(schedule)));
        when(studyRepository.findByIdWithDetails(studyId)).thenReturn(Optional.of(study));

        // when
        StudyProgressResponseDTO result = studyProgressService.getStudyProgress(request);

        // then
        assertThat(result.getStudyName()).isEqualTo("Test Study");
        assertThat(result.getSchedules()).hasSize(1);
        assertThat(result.getSchedules().get(0).getSections()).isEmpty();

        verify(studyRepository).findByIdWithDetails(studyId);
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

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(10L);
        when(schedule.getName()).thenReturn("Test Schedule");
        when(schedule.getDescription()).thenReturn("테스트");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(true);

        Section section = mock(Section.class);
        when(section.getId()).thenReturn(100L);
        when(section.getSectionName()).thenReturn("Test Section");
        when(section.getSectionNumber()).thenReturn(1L);
        when(section.getNeedsReview()).thenReturn(true);
        when(section.getSchedule()).thenReturn(schedule);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1000L);
        when(user1.getUsername()).thenReturn("김철수");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2000L);
        when(user2.getUsername()).thenReturn("이영희");

        // user1은 회고 제출, user2는 심화학습 제출
        Review review = mock(Review.class);
        when(review.getUser()).thenReturn(user1);
        when(review.getSection()).thenReturn(section);

        DeepStudy deepStudy = mock(DeepStudy.class);
        when(deepStudy.getUser()).thenReturn(user2);
        when(deepStudy.getSchedule()).thenReturn(schedule);

        // Fetch Join으로 로드된 데이터 설정
        when(study.getSchedules()).thenReturn(new HashSet<>(Arrays.asList(schedule)));
        when(schedule.getSections()).thenReturn(Arrays.asList(section));
        when(schedule.getDeepStudies()).thenReturn(new HashSet<>(Arrays.asList(deepStudy)));
        when(section.getReviews()).thenReturn(new HashSet<>(Arrays.asList(review)));

        when(studyRepository.findByIdWithDetails(studyId)).thenReturn(Optional.of(study));

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
        
        // verify
        verify(studyRepository).findByIdWithDetails(studyId);
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

        // 스케줄 mock
        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(10L);
        when(schedule.getName()).thenReturn("Section 2~4");
        when(schedule.getDescription()).thenReturn("스프링 기초");
        when(schedule.getScheduleDate()).thenReturn(LocalDateTime.of(2025, 1, 15, 20, 0));
        when(schedule.getMeetingLocation()).thenReturn("온라인");
        when(schedule.getHasDeepStudy()).thenReturn(true);

        // 섹션 mock
        Section section = mock(Section.class);
        when(section.getId()).thenReturn(100L);
        when(section.getSectionName()).thenReturn("Section 2");
        when(section.getSectionNumber()).thenReturn(2L);
        when(section.getNeedsReview()).thenReturn(true);
        when(section.getSchedule()).thenReturn(schedule);

        // 사용자 mock
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("김철수");

        // 회고 mock
        Review review = mock(Review.class);
        when(review.getUser()).thenReturn(user);
        when(review.getSection()).thenReturn(section);

        // 심화학습 mock
        DeepStudy deepStudy = mock(DeepStudy.class);
        when(deepStudy.getUser()).thenReturn(user);
        when(deepStudy.getSchedule()).thenReturn(schedule);

        // Fetch Join으로 로드된 데이터 설정
        when(study.getSchedules()).thenReturn(Collections.singleton(schedule));
        when(schedule.getSections()).thenReturn(Collections.singletonList(section));
        when(schedule.getDeepStudies()).thenReturn(Collections.singleton(deepStudy));
        when(section.getReviews()).thenReturn(Collections.singleton(review));

        when(studyRepository.findByIsActiveTrueWithDetails()).thenReturn(Optional.of(study));

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
        verify(studyRepository).findByIsActiveTrueWithDetails();
    }

    @Test
    @DisplayName("현재 진행 중인 스터디 진행 현황 조회 실패 - 진행 중인 스터디 없음")
    void getCurrentStudyProgress_fail_noActiveStudy() {
        // given
        StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                .studyId(null)
                .build();

        when(studyRepository.findByIsActiveTrueWithDetails()).thenReturn(Optional.empty());

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
