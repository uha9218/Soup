package com.example.soup.study.service;

import com.example.soup.study.dto.StudyProgressRequestDTO;
import com.example.soup.study.dto.StudyProgressResponseDTO;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.section.entity.Section;
import com.example.soup.user.entity.User;
import com.example.soup.review.entity.Review;
import com.example.soup.deepstudy.entity.DeepStudy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StudyProgressService {

    private final StudyRepository studyRepository;

    @Transactional(readOnly = true)
    public StudyProgressResponseDTO getStudyProgress(StudyProgressRequestDTO request) {
        // 1. Fetch Join을 사용하여 스터디와 모든 연관 데이터를 한 번에 조회
        Study study;
        if (request.getStudyId() == null || request.getStudyId() == 0) {
            study = studyRepository.findByIsActiveTrueWithDetails()
                    .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 스터디가 없습니다."));
        } else {
            study = studyRepository.findByIdWithDetails(request.getStudyId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));
        }

        // 2. 조회된 데이터에서 스터디에 참여한 모든 유저를 추출 (중복 제거)
        // userRepository.findAll() 대신, 실제 참여한 유저만 필터링하여 불필요한 전체 조회를 방지합니다.
        Set<User> studyUsers = study.getSchedules().stream()
                .flatMap(schedule -> Stream.concat(
                        schedule.getSections().stream()
                                .flatMap(section -> section.getReviews().stream())
                                .map(Review::getUser),
                        schedule.getDeepStudies().stream()
                                .map(DeepStudy::getUser)
                ))
                .collect(Collectors.toSet());

        // 3. 이미 로드된 데이터를 DTO로 변환 (추가 쿼리 발생 없음)
        List<StudyProgressResponseDTO.ScheduleProgressDTO> scheduleProgressList = study.getSchedules().stream()
                .map(schedule -> buildScheduleProgressDTO(schedule, studyUsers))
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.of(study, scheduleProgressList);
    }

    // 현재 진행 중인 스터디 조회 메서드 추가
    @Transactional(readOnly = true)
    public Study getCurrentActiveStudy() {
        return studyRepository.findByIsActiveTrue()
                .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 스터디가 없습니다."));
    }

    private StudyProgressResponseDTO.ScheduleProgressDTO buildScheduleProgressDTO(Schedule schedule, Set<User> studyUsers) {
        // Fetch Join으로 이미 Section 데이터가 로드되었으므로 DB 접근이 필요 없습니다.
        List<StudyProgressResponseDTO.SectionProgressDTO> sectionProgressList = schedule.getSections().stream()
                .map(section -> buildSectionProgressDTO(section, studyUsers))
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.ScheduleProgressDTO.of(schedule, sectionProgressList);
    }

    private StudyProgressResponseDTO.SectionProgressDTO buildSectionProgressDTO(Section section, Set<User> studyUsers) {
        // userRepository.findAll() 대신, 미리 추출한 studyUsers를 사용합니다.
        List<StudyProgressResponseDTO.MemberProgressDTO> memberProgressList = studyUsers.stream()
                .map(user -> buildMemberProgressDTO(user, section))
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.SectionProgressDTO.of(section, memberProgressList);
    }

    private StudyProgressResponseDTO.MemberProgressDTO buildMemberProgressDTO(User user, Section section) {
        // Fetch Join으로 이미 Review 데이터가 로드되었으므로 DB 접근이 필요 없습니다.
        boolean reviewSubmitted = section.getReviews().stream()
                .anyMatch(review -> review.getUser().getId().equals(user.getId()));

        // Fetch Join으로 이미 DeepStudy 데이터가 로드되었으므로 DB 접근이 필요 없습니다.
        boolean deepStudySubmitted = section.getSchedule().getDeepStudies().stream()
                .anyMatch(deepStudy -> deepStudy.getUser().getId().equals(user.getId()));

        return StudyProgressResponseDTO.MemberProgressDTO.of(
                user,
                reviewSubmitted,
                deepStudySubmitted
        );
    }
}
