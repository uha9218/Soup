package com.example.soup.schedule.controller;

import com.example.soup.schedule.dto.ScheduleCalendarRequestDTO;
import com.example.soup.schedule.dto.ScheduleCalendarResponseDTO;
import com.example.soup.schedule.service.ScheduleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vue.js 개발 서버
public class ScheduleCalendarController {

    private final ScheduleCalendarService scheduleCalendarService;

    /**
     * 월별 일정 조회 API
     * @param request 년도와 월 정보
     * @return 해당 월의 일정 목록
     */
    @PostMapping("/calendar")
    public ResponseEntity<ScheduleCalendarResponseDTO> getMonthlySchedules(
            @RequestBody ScheduleCalendarRequestDTO request) {
        ScheduleCalendarResponseDTO response = scheduleCalendarService.getMonthlySchedules(request);
        return ResponseEntity.ok(response);
    }
}
