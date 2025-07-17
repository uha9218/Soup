package com.example.soup.admin.study.controller;


import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.example.soup.admin.study.dto.AdminStudyResponseDTO;
import com.example.soup.admin.study.dto.AdminStudyDeleteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/study")
@RequiredArgsConstructor
public class AdminStudyController {

	@PostMapping
	public ResponseEntity<AdminStudyResponseDTO> createStudy(@Valid @RequestBody AdminStudyRequestDTO.Create request) {
		AdminStudyResponseDTO response = AdminStudyResponseDTO.of(
			request.getName(),
			request.getDescription(),
			request.getType(),
			request.getPeriod(),
			false,
			null
		);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<AdminStudyResponseDTO> updateStudy(@Valid @RequestBody AdminStudyRequestDTO.Update request) {
		AdminStudyResponseDTO response = AdminStudyResponseDTO.of(
			request.getName(),
			request.getDescription(),
			request.getType(),
			request.getPeriod(),
			request.getIsCompleted(),
			null
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<AdminStudyResponseDTO> getStudy() {
		AdminStudyResponseDTO response = AdminStudyResponseDTO.of(
			"Dummy Study",
			"This is a dummy description",
			"Online",
			"3 months",
			true,
			null
		);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	public ResponseEntity<AdminStudyDeleteResponseDTO> deleteStudy(@RequestParam String name) {
		AdminStudyDeleteResponseDTO response = AdminStudyDeleteResponseDTO.of(name);
		return ResponseEntity.ok(response);
	}
}
