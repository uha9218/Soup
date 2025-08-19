package com.example.soup.deepstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soup.deepstudy.entity.DeepStudy;

import java.util.List;

public interface DeepStudyRepository extends JpaRepository<DeepStudy, Long> {
    List<DeepStudy> findByScheduleId(Long scheduleId);
}
