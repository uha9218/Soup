package com.example.soup.study.repository;

import com.example.soup.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Optional<Study> findByIsActiveTrue();

    /**
     * 특정 스터디와 관련된 모든 연관 관계(스케줄, 섹션, 리뷰, 심화학습 및 관련 유저)를
     * 단일 쿼리로 조회하여 N+1 문제를 해결합니다. (Fetch Join 적용)
     * 'DISTINCT'를 사용하여 카티전 곱으로 인해 발생하는 Study 루트 엔티티의 중복을 제거합니다.
     */
    @Query("SELECT DISTINCT s FROM Study s " +
           "LEFT JOIN FETCH s.schedules sched " +
           "LEFT JOIN FETCH sched.sections sect " +
           "LEFT JOIN FETCH sect.reviews r " +
           "LEFT JOIN FETCH r.user ru " +
           "LEFT JOIN FETCH sched.deepStudies ds " +
           "LEFT JOIN FETCH ds.user dsu " +
           "WHERE s.id = :studyId")
    Optional<Study> findByIdWithDetails(@Param("studyId") Long studyId);

    @Query("SELECT DISTINCT s FROM Study s " +
           "LEFT JOIN FETCH s.schedules sched " +
           "LEFT JOIN FETCH sched.sections sect " +
           "LEFT JOIN FETCH sect.reviews r " +
           "LEFT JOIN FETCH r.user ru " +
           "LEFT JOIN FETCH sched.deepStudies ds " +
           "LEFT JOIN FETCH ds.user dsu " +
           "WHERE s.isActive = true")
    Optional<Study> findByIsActiveTrueWithDetails();
}
