package com.example.soup.admin.study.controller;


import java.util.List;

import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.example.soup.admin.study.dto.AdminStudyResponseDTO;
import com.example.soup.admin.study.dto.AdminStudyDeleteResponseDTO;
import com.example.soup.admin.study.service.AdminStudyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/studies")
@RequiredArgsConstructor
public class AdminStudyController {

	private final AdminStudyService studyService;

	@PostMapping
	public ResponseEntity<AdminStudyResponseDTO> createStudy(
		@RequestBody AdminStudyRequestDTO.Create request) {
		return ResponseEntity.ok(studyService.createStudy(request));
	}
	@GetMapping
	public ResponseEntity<List<AdminStudyResponseDTO>> getAllStudies() {
		return ResponseEntity.ok(studyService.getAllStudies());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AdminStudyResponseDTO> getStudy(@PathVariable Long id) {
		return ResponseEntity.ok(studyService.getStudy(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AdminStudyResponseDTO> updateStudy(
		@PathVariable Long id,
		@RequestBody AdminStudyRequestDTO.Update request) {
		return ResponseEntity.ok(studyService.updateStudy(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<AdminStudyDeleteResponseDTO> deleteStudy(@PathVariable Long id) {
		return ResponseEntity.ok(studyService.deleteStudy(id));
	}
}