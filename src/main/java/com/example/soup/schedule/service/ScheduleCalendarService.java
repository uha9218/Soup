package com.example.soup.schedule.service;

import com.example.soup.annotation.LogExecutionTime;
import com.example.soup.schedule.dto.ScheduleCalendarRequestDTO;
import com.example.soup.schedule.dto.ScheduleCalendarResponseDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleCalendarService {
    
    private final ScheduleRepository scheduleRepository;
    
    @LogExecutionTime
    public ScheduleCalendarResponseDTO getMonthlySchedules(ScheduleCalendarRequestDTO request) {
        log.info("=== getMonthlySchedules 시작 ===");
        log.info("요청 데이터: year={}, month={}", request.getYear(), request.getMonth());
        
        validateRequest(request);
        
        LocalDateTime startOfMonth = LocalDateTime.of(request.getYear(), request.getMonth(), 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
        
        log.info("조회 기간: {} ~ {}", startOfMonth, endOfMonth);
        
        long startTime = System.currentTimeMillis();
        List<Schedule> schedules = scheduleRepository.findByScheduleDateBetweenOrderByScheduleDate(startOfMonth, endOfMonth);
        long endTime = System.currentTimeMillis();
        
        log.info("데이터베이스 조회 완료: {}개 일정, 소요시간: {}ms", schedules.size(), endTime - startTime);
        
        // DTO의 of() 메서드를 사용하여 변환 위임
        List<ScheduleCalendarResponseDTO.ScheduleInfo> scheduleInfos = schedules.stream()
                .map(ScheduleCalendarResponseDTO.ScheduleInfo::of)
                .collect(Collectors.toList());
        
        log.info("=== getMonthlySchedules 완료: {}개 일정 반환 ===", scheduleInfos.size());
        
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