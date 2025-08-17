package com.example.soup.schedule.service;

import com.example.soup.schedule.dto.ScheduleCalendarRequestDTO;
import com.example.soup.schedule.dto.ScheduleCalendarResponseDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleCalendarService {
    
    private final ScheduleRepository scheduleRepository;
    
    public ScheduleCalendarResponseDTO getMonthlySchedules(ScheduleCalendarRequestDTO request) {
        validateRequest(request);
        
        LocalDateTime startOfMonth = LocalDateTime.of(request.getYear(), request.getMonth(), 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
        
        List<Schedule> schedules = scheduleRepository.findByScheduleDateBetweenOrderByScheduleDate(startOfMonth, endOfMonth);
        
        // DTO의 of() 메서드를 사용하여 변환 위임
        List<ScheduleCalendarResponseDTO.ScheduleInfo> scheduleInfos = schedules.stream()
                .map(ScheduleCalendarResponseDTO.ScheduleInfo::of)
                .collect(Collectors.toList());
        
        return ScheduleCalendarResponseDTO.builder()
                .schedules(scheduleInfos)
                .build();
    }
    
    private void validateRequest(ScheduleCalendarRequestDTO request) {
        if (request.getYear() == null || request.getMonth() == null) {
            throw new IllegalArgumentException("년도와 월은 필수 입력값입니다.");
        }
        
        if (request.getYear() < 1900 || request.getYear() > 2100) {
            throw new IllegalArgumentException("년도는 1900년부터 2100년 사이여야 합니다.");
        }
        
        if (request.getMonth() < 1 || request.getMonth() > 12) {
            throw new IllegalArgumentException("월은 1월부터 12월 사이여야 합니다.");
        }
    }
}