package com.example.soup.study.controller;

import com.example.soup.study.dto.StudyProgressRequestDTO;
import com.example.soup.study.dto.StudyProgressResponseDTO;
import com.example.soup.study.service.StudyProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/study-progress")
@RequiredArgsConstructor
public class StudyProgressController {

    private final StudyProgressService studyProgressService;

    @GetMapping("/{studyId}")
    public ResponseEntity<?> getStudyProgress(@PathVariable Long studyId) {
        try {
            StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                    .studyId(studyId)
                    .build();
            StudyProgressResponseDTO response = studyProgressService.getStudyProgress(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentStudyProgress() {
        try {
            StudyProgressRequestDTO request = StudyProgressRequestDTO.builder()
                    .studyId(null) // null로 설정하면 현재 진행 중인 스터디를 자동으로 찾음
                    .build();
            StudyProgressResponseDTO response = studyProgressService.getStudyProgress(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
