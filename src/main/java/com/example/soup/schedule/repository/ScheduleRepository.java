package com.example.soup.schedule.repository;

import com.example.soup.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleDateBetweenOrderByScheduleDate(LocalDateTime startDate, LocalDateTime endDate);
    List<Schedule> findByStudyId(Long studyId);
}
