package com.example.soup.admin.schedule.service;

import java.util.List;

import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminScheduleService {

	private final ScheduleRepository scheduleRepository;

	@Transactional
	public AdminScheduleResponseDTO createSchedule(AdminScheduleRequestDTO request) {
		Schedule schedule = Schedule.create(
			request.getTitle(),
			request.getType(),
			request.getStudyId(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);

		Schedule saved = scheduleRepository.save(schedule);

		return AdminScheduleResponseDTO.of(
			saved.getTitle(),
			saved.getType(),
			saved.getStudyId(),
			saved.getSectionIds(),
			saved.getMeetingLink(),
			saved.getScheduleDateTime()
		);
	}
	@Transactional(readOnly = true)
	public List<AdminScheduleResponseDTO> getAllSchedules() {
		List<Schedule> schedules = scheduleRepository.findAll();

		return schedules.stream()
			.map(schedule -> AdminScheduleResponseDTO.of(
				schedule.getTitle(),
				schedule.getType(),
				schedule.getStudyId(),
				schedule.getSectionIds(),
				schedule.getMeetingLink(),
				schedule.getScheduleDateTime()
			))
			.toList();
	}

	@Transactional(readOnly = true)
	public AdminScheduleResponseDTO getSchedule(Long id) {
		Schedule schedule = scheduleRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

		return AdminScheduleResponseDTO.of(
			schedule.getTitle(),
			schedule.getType(),
			schedule.getStudyId(),
			schedule.getSectionIds(),
			schedule.getMeetingLink(),
			schedule.getScheduleDateTime()
		);
	}

	@Transactional
	public AdminScheduleResponseDTO updateSchedule(Long id, AdminScheduleRequestDTO request) {
		Schedule schedule = scheduleRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

		schedule.update(
			request.getTitle(),
			request.getType(),
			request.getSectionIds(),
			request.getMeetingLink(),
			request.getScheduleDateTime()
		);

		return AdminScheduleResponseDTO.of(
			schedule.getTitle(),
			schedule.getType(),
			schedule.getStudyId(),
			schedule.getSectionIds(),
			schedule.getMeetingLink(),
			schedule.getScheduleDateTime()
		);
	}

	@Transactional
	public AdminScheduleDeleteResponseDTO deleteSchedule(Long id) {
		Schedule schedule = scheduleRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

		String title = schedule.getTitle();
		scheduleRepository.delete(schedule);

		return AdminScheduleDeleteResponseDTO.of(title);
	}
}
