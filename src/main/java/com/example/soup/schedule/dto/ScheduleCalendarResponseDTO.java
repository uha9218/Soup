package com.example.soup.schedule.dto;

import com.example.soup.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCalendarResponseDTO {
    private List<ScheduleInfo> schedules;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleInfo {
        private Long id;
        private String title;
        private String date;
        private String time;
        private String description;
        private String meetingLocation;
        
        // Entity → DTO 변환 역할을 DTO에서 담당
        public static ScheduleInfo of(Schedule schedule) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            return ScheduleInfo.builder()
                    .id(schedule.getId())
                    .title(schedule.getName())
                    .date(schedule.getScheduleDate().format(dateFormatter))
                    .time(schedule.getScheduleDate().format(timeFormatter))
                    .description(schedule.getDescription())
                    .meetingLocation(schedule.getMeetingLocation())
                    .build();
        }
    }
}