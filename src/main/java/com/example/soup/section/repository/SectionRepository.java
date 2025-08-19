package com.example.soup.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soup.section.entity.Section;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByScheduleId(Long scheduleId);
}
