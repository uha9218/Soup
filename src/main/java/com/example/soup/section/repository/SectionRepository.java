package com.example.soup.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.soup.section.entity.Section;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByScheduleId(Long scheduleId);
    
    /**
     * 특정 스케줄의 모든 섹션과 관련 리뷰, 리뷰 작성자를 한 번에 조회합니다.
     * 2-Step Inquiry 패턴을 위한 최적화된 쿼리입니다.
     */
    @Query("SELECT DISTINCT s FROM Section s " +
           "LEFT JOIN FETCH s.reviews r " +
           "LEFT JOIN FETCH r.user " +
           "WHERE s.schedule.id = :scheduleId")
    List<Section> findByScheduleIdWithReviews(@Param("scheduleId") Long scheduleId);
}
