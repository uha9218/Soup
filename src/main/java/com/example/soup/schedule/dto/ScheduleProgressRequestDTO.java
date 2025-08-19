package com.example.soup.schedule.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleProgressRequestDTO {
    private Long scheduleId;  // 스케줄 ID
}
