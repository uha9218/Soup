package com.example.soup.study.service;

import com.example.soup.study.dto.StudyProgressRequestDTO;
import com.example.soup.study.dto.StudyProgressResponseDTO;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyProgressService {

    private final StudyRepository studyRepository;
    private final ScheduleRepository scheduleRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final DeepStudyRepository deepStudyRepository;

    @Transactional(readOnly = true)
    public StudyProgressResponseDTO getStudyProgress(StudyProgressRequestDTO request) {
        // 1. 현재 진행 중인 스터디 조회 (studyId가 null이거나 0인 경우)
        Study study;
        if (request.getStudyId() == null || request.getStudyId() == 0) {
            study = studyRepository.findByIsActiveTrue()
                    .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 스터디가 없습니다."));
        } else {
            study = studyRepository.findById(request.getStudyId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));
        }

        // 2. 해당 스터디의 모든 스케줄 조회
        List<Schedule> schedules = scheduleRepository.findByStudyId(study.getId());

        // 3. 각 스케줄의 섹션들과 팀원 진행 현황 조회
        List<StudyProgressResponseDTO.ScheduleProgressDTO> scheduleProgressList = schedules.stream()
                .map(this::buildScheduleProgressDTO)
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.of(study, scheduleProgressList);
    }

    // 현재 진행 중인 스터디 조회 메서드 추가
    @Transactional(readOnly = true)
    public Study getCurrentActiveStudy() {
        return studyRepository.findByIsActiveTrue()
                .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 스터디가 없습니다."));
    }

    private StudyProgressResponseDTO.ScheduleProgressDTO buildScheduleProgressDTO(Schedule schedule) {
        // 1. 스케줄의 모든 섹션 조회
        List<Section> sections = sectionRepository.findByScheduleId(schedule.getId());

        // 2. 각 섹션의 팀원 진행 현황 조회
        List<StudyProgressResponseDTO.SectionProgressDTO> sectionProgressList = sections.stream()
                .map(this::buildSectionProgressDTO)
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.ScheduleProgressDTO.of(schedule, sectionProgressList);
    }

    private StudyProgressResponseDTO.SectionProgressDTO buildSectionProgressDTO(Section section) {
        // 1. 모든 팀원 조회 (실제로는 스터디별 팀원 조회가 필요할 수 있음)
        List<User> users = userRepository.findAll();

        // 2. 각 팀원의 진행 현황 조회
        List<StudyProgressResponseDTO.MemberProgressDTO> memberProgressList = users.stream()
                .map(user -> buildMemberProgressDTO(user, section))
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.SectionProgressDTO.of(section, memberProgressList);
    }

    private StudyProgressResponseDTO.MemberProgressDTO buildMemberProgressDTO(User user, Section section) {
        // 1. 해당 섹션의 회고 제출 여부 확인
        List<Review> reviews = reviewRepository.findBySectionId(section.getId());
        boolean reviewSubmitted = reviews.stream()
                .anyMatch(review -> review.getUser().getId().equals(user.getId()));

        // 2. 해당 스케줄의 심화학습 제출 여부 확인
        List<DeepStudy> deepStudies = deepStudyRepository.findByScheduleId(section.getSchedule().getId());
        boolean deepStudySubmitted = deepStudies.stream()
                .anyMatch(deepStudy -> deepStudy.getUser().getId().equals(user.getId()));

        return StudyProgressResponseDTO.MemberProgressDTO.of(
                user,
                reviewSubmitted,
                deepStudySubmitted
        );
    }
}
