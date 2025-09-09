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
import com.example.soup.deepstudy.entity.DeepStudy;
import com.example.soup.deepstudy.repository.DeepStudyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyProgressService {

    private final StudyRepository studyRepository;
    private final ScheduleRepository scheduleRepository;
    private final SectionRepository sectionRepository;
    private final DeepStudyRepository deepStudyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StudyProgressResponseDTO getStudyProgress(StudyProgressRequestDTO request) {
        // 2-Step Inquiry 패턴 적용: 단일 스케줄에 대한 최적화된 조회
        
        // Step 1: 기본 엔티티들 조회
        Study study = getStudyById(request.getStudyId());
        Schedule schedule = getScheduleById(request.getScheduleId());
        List<User> studyUsers = getStudyUsers(request.getStudyId());
        
        // Step 2: 필요한 컬렉션 데이터들을 효율적으로 조회
        List<Section> sectionsWithReviews = sectionRepository.findByScheduleIdWithReviews(request.getScheduleId());
        List<DeepStudy> deepStudiesWithUsers = deepStudyRepository.findByScheduleIdWithUser(request.getScheduleId());
        
        // Step 3: 빠른 조회를 위한 Map 생성
        Map<Long, Map<Long, Review>> reviewMap = buildReviewMap(sectionsWithReviews);
        Map<Long, DeepStudy> deepStudyMap = buildDeepStudyMap(deepStudiesWithUsers);
        
        // Step 4: DTO 변환
        StudyProgressResponseDTO.ScheduleProgressDTO scheduleProgress = 
                buildScheduleProgressDTO(schedule, sectionsWithReviews, studyUsers, reviewMap, deepStudyMap);
        
        return StudyProgressResponseDTO.of(study, List.of(scheduleProgress));
    }

    // 현재 진행 중인 스터디 조회 메서드 추가
    @Transactional(readOnly = true)
    public Study getCurrentActiveStudy() {
        return studyRepository.findByIsActiveTrue()
                .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 스터디가 없습니다."));
    }
    
    // 2-Step Inquiry 패턴을 위한 헬퍼 메서드들
    
    private Study getStudyById(Long studyId) {
        if (studyId == null || studyId == 0) {
            return studyRepository.findByIsActiveTrue()
                    .orElseThrow(() -> new IllegalArgumentException("현재 진행 중인 스터디가 없습니다."));
        } else {
            return studyRepository.findById(studyId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));
        }
    }
    
    private Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));
    }
    
    private List<User> getStudyUsers(Long studyId) {
        // 스터디에 참여한 모든 사용자를 조회
        // 실제 구현에서는 스터디 참여자 테이블이 있다면 그곳에서 조회하는 것이 더 효율적입니다.
        return userRepository.findAll();
    }
    
    private Map<Long, Map<Long, Review>> buildReviewMap(List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toMap(
                        Section::getId,
                        section -> section.getReviews().stream()
                                .collect(Collectors.toMap(
                                        review -> review.getUser().getId(),
                                        review -> review
                                ))
                ));
    }
    
    private Map<Long, DeepStudy> buildDeepStudyMap(List<DeepStudy> deepStudies) {
        return deepStudies.stream()
                .collect(Collectors.toMap(
                        deepStudy -> deepStudy.getUser().getId(),
                        deepStudy -> deepStudy
                ));
    }

    private StudyProgressResponseDTO.ScheduleProgressDTO buildScheduleProgressDTO(
            Schedule schedule, 
            List<Section> sections, 
            List<User> studyUsers,
            Map<Long, Map<Long, Review>> reviewMap,
            Map<Long, DeepStudy> deepStudyMap) {
        
        List<StudyProgressResponseDTO.SectionProgressDTO> sectionProgressList = sections.stream()
                .map(section -> buildSectionProgressDTO(section, studyUsers, reviewMap, deepStudyMap))
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.ScheduleProgressDTO.of(schedule, sectionProgressList);
    }

    private StudyProgressResponseDTO.SectionProgressDTO buildSectionProgressDTO(
            Section section, 
            List<User> studyUsers,
            Map<Long, Map<Long, Review>> reviewMap,
            Map<Long, DeepStudy> deepStudyMap) {
        
        List<StudyProgressResponseDTO.MemberProgressDTO> memberProgressList = studyUsers.stream()
                .map(user -> buildMemberProgressDTO(user, section, reviewMap, deepStudyMap))
                .collect(Collectors.toList());

        return StudyProgressResponseDTO.SectionProgressDTO.of(section, memberProgressList);
    }

    private StudyProgressResponseDTO.MemberProgressDTO buildMemberProgressDTO(
            User user, 
            Section section,
            Map<Long, Map<Long, Review>> reviewMap,
            Map<Long, DeepStudy> deepStudyMap) {
        
        // Map을 사용하여 O(1) 시간복잡도로 조회
        boolean reviewSubmitted = reviewMap.getOrDefault(section.getId(), Map.of())
                .containsKey(user.getId());

        boolean deepStudySubmitted = deepStudyMap.containsKey(user.getId());

        return StudyProgressResponseDTO.MemberProgressDTO.of(
                user,
                reviewSubmitted,
                deepStudySubmitted
        );
    }
}
