package com.example.soup.deepstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.soup.deepstudy.entity.DeepStudy;

import java.util.List;

public interface DeepStudyRepository extends JpaRepository<DeepStudy, Long> {
    List<DeepStudy> findByScheduleId(Long scheduleId);
    List<DeepStudy> findByScheduleIdIn(List<Long> scheduleIds);
    
    /**
     * 특정 스케줄의 모든 심화학습과 관련 사용자를 한 번에 조회합니다.
     * 2-Step Inquiry 패턴을 위한 최적화된 쿼리입니다.
     */
    @Query("SELECT DISTINCT d FROM DeepStudy d " +
           "LEFT JOIN FETCH d.user " +
           "WHERE d.schedule.id = :scheduleId")
    List<DeepStudy> findByScheduleIdWithUser(@Param("scheduleId") Long scheduleId);
}
