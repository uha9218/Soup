package com.example.soup.admin.schedule.controller;

import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/schedule")
@RequiredArgsConstructor
public class AdminScheduleController {

	@PostMapping
	public ResponseEntity<AdminScheduleResponseDTO> createSchedule(@Valid @RequestBody AdminScheduleRequestDTO request) {
		AdminScheduleResponseDTO response = AdminScheduleResponseDTO.of(
			request.getTitle(),
			request.getType(),
			request.getStudyId(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<AdminScheduleResponseDTO> updateSchedule(@Valid @RequestBody AdminScheduleRequestDTO request) {
		AdminScheduleResponseDTO response = AdminScheduleResponseDTO.of(
			request.getTitle(),
			request.getType(),
			request.getStudyId(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<AdminScheduleResponseDTO> getSchedule() {
		AdminScheduleResponseDTO response = AdminScheduleResponseDTO.of(
			"더미 일정",
			"온라인",
			100L,
			List.of(1L, 2L),
			"https://zoom.us/example",
			LocalDateTime.now()
		);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping
	public ResponseEntity<AdminScheduleDeleteResponseDTO> deleteSchedule(@RequestParam String title) {
		AdminScheduleDeleteResponseDTO response = AdminScheduleDeleteResponseDTO.of(title);
		return ResponseEntity.ok(response);
	}
}
