package com.example.soup.study.dto;

import com.example.soup.study.entity.Study;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.section.entity.Section;
import com.example.soup.user.entity.User;
import com.example.soup.review.entity.Review;
import com.example.soup.deepstudy.entity.DeepStudy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyProgressResponseDTO {
    private String studyName;                    // 스터디명
    private List<ScheduleProgressDTO> schedules; // 스케줄 목록
    
    // Entity → DTO 변환
    public static StudyProgressResponseDTO of(Study study, List<ScheduleProgressDTO> schedules) {
        return StudyProgressResponseDTO.builder()
                .studyName(study.getName())
                .schedules(schedules)
                .build();
    }
    
    // 스케줄 진행 현황 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleProgressDTO {
        private Long scheduleId;                     // 스케줄 ID
        private String scheduleName;                 // 스케줄명
        private String scheduleDate;                 // 스케줄 날짜
        private String scheduleTime;                 // 스케줄 시간
        private String description;                  // 설명
        private String meetingLocation;              // 모임 장소
        private Boolean hasDeepStudy;                // 심화학습 포함 여부
        private List<SectionProgressDTO> sections;   // 섹션 목록
        
        // Entity → DTO 변환
        public static ScheduleProgressDTO of(Schedule schedule, List<SectionProgressDTO> sections) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            return ScheduleProgressDTO.builder()
                    .scheduleId(schedule.getId())
                    .scheduleName(schedule.getName())
                    .scheduleDate(schedule.getScheduleDate().format(dateFormatter))
                    .scheduleTime(schedule.getScheduleDate().format(timeFormatter))
                    .description(schedule.getDescription())
                    .meetingLocation(schedule.getMeetingLocation())
                    .hasDeepStudy(schedule.getHasDeepStudy())
                    .sections(sections)
                    .build();
        }
    }
    
    // 섹션 진행 현황 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionProgressDTO {
        private Long sectionId;                     // 섹션 ID
        private String sectionName;                 // 섹션명
        private Long sectionNumber;                 // 섹션 번호
        private Boolean needsReview;                // 회고 필요 여부
        private List<MemberProgressDTO> members;    // 팀원 목록
        
        // Entity → DTO 변환
        public static SectionProgressDTO of(Section section, List<MemberProgressDTO> members) {
            return SectionProgressDTO.builder()
                    .sectionId(section.getId())
                    .sectionName(section.getSectionName())
                    .sectionNumber(section.getSectionNumber())
                    .needsReview(section.getNeedsReview())
                    .members(members)
                    .build();
        }
    }
    
    // 팀원 진행 현황 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProgressDTO {
        private Long userId;                        // 사용자 ID
        private String userName;                    // 사용자명
        private Boolean reviewSubmitted;            // 회고 제출 여부
        private Boolean deepStudySubmitted;         // 심화학습 제출 여부
        
        // Entity → DTO 변환
        public static MemberProgressDTO of(User user, Boolean reviewSubmitted, Boolean deepStudySubmitted) {
            return MemberProgressDTO.builder()
                    .userId(user.getId())
                    .userName(user.getUsername())
                    .reviewSubmitted(reviewSubmitted)
                    .deepStudySubmitted(deepStudySubmitted)
                    .build();
        }
    }
}
