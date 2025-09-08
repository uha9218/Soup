package com.example.soup.study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyProgressRequestDTO {
    private Long studyId;     // 스터디 ID
    private Long scheduleId;  // 스케줄 ID (2-Step Inquiry 패턴을 위해 추가)
}
