package com.example.soup.admin.section.controller;

import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/section")
@RequiredArgsConstructor
public class AdminSectionController {

	@PostMapping
	public ResponseEntity<AdminSectionResponseDTO> createSection(@Valid @RequestBody AdminSectionRequestDTO request) {
		AdminSectionResponseDTO response = AdminSectionResponseDTO.of(
			request.getSectionNumber(),
			request.getSectionName(),
			request.getStudyId()
		);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<AdminSectionResponseDTO> updateSection(@Valid @RequestBody AdminSectionRequestDTO request) {
		AdminSectionResponseDTO response = AdminSectionResponseDTO.of(
			request.getSectionNumber(),
			request.getSectionName(),
			request.getStudyId()
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<AdminSectionResponseDTO> getSection() {
		AdminSectionResponseDTO response = AdminSectionResponseDTO.of(
			1L,
			"섹션 이름",
			100L
		);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	public ResponseEntity<AdminSectionDeleteResponseDTO> deleteSection(@RequestParam Long sectionNumber, @RequestParam String sectionName) {
		AdminSectionDeleteResponseDTO response = AdminSectionDeleteResponseDTO.of(
			sectionNumber,
			sectionName
		);
		return ResponseEntity.ok(response);
	}
}
