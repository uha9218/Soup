package com.example.soup.admin.section.controller;

import java.util.List;

import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import com.example.soup.admin.section.service.AdminSectionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sections")
@RequiredArgsConstructor
public class AdminSectionController {

	private final AdminSectionService sectionService;

	@PostMapping
	public ResponseEntity<AdminSectionResponseDTO> createSection(
		@RequestBody AdminSectionRequestDTO.Create request) {
		return ResponseEntity.ok(sectionService.createSection(request));
	}

	@GetMapping
	public ResponseEntity<List<AdminSectionResponseDTO>> getAllSections() {
		return ResponseEntity.ok(sectionService.getAllSections());
	}


	@GetMapping("/{id}")
	public ResponseEntity<AdminSectionResponseDTO> getSection(@PathVariable Long id) {
		return ResponseEntity.ok(sectionService.getSection(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AdminSectionResponseDTO> updateSection(
		@PathVariable Long id,
		@RequestBody AdminSectionRequestDTO.Update request) {
		return ResponseEntity.ok(sectionService.updateSection(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<AdminSectionDeleteResponseDTO> deleteSection(@PathVariable Long id) {
		return ResponseEntity.ok(sectionService.deleteSection(id));
	}
}
