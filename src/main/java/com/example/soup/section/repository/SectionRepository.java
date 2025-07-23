package com.example.soup.section.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.soup.section.entity.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
