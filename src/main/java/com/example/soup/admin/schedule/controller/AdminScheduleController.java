package com.example.soup.admin.schedule.controller;

import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import com.example.soup.admin.schedule.service.AdminScheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

	private final AdminScheduleService scheduleService;

	@PostMapping
	public ResponseEntity<AdminScheduleResponseDTO> createSchedule(
		@RequestBody AdminScheduleRequestDTO request) {
		return ResponseEntity.ok(scheduleService.createSchedule(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AdminScheduleResponseDTO> getSchedule(@PathVariable Long id) {
		return ResponseEntity.ok(scheduleService.getSchedule(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AdminScheduleResponseDTO> updateSchedule(
		@PathVariable Long id,
		@RequestBody AdminScheduleRequestDTO request) {
		return ResponseEntity.ok(scheduleService.updateSchedule(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<AdminScheduleDeleteResponseDTO> deleteSchedule(@PathVariable Long id) {
		return ResponseEntity.ok(scheduleService.deleteSchedule(id));
	}
}
