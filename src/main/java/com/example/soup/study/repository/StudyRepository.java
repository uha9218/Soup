package com.example.soup.study.repository;


import com.example.soup.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    // 현재 진행 중인 스터디 조회
    Optional<Study> findByIsActiveTrue();
    
    // 현재 진행 중인 스터디 목록 조회 (여러 개일 경우)
    List<Study> findByIsActiveTrueOrderByCreatedAtDesc();
}
