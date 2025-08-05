package com.example.soup.admin.schedule.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleResponseDTO;
import com.example.soup.admin.schedule.dto.AdminScheduleDeleteResponseDTO;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class AdminScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final StudyRepository studyRepository;
	private final SectionRepository sectionRepository;

	@Transactional
	public AdminScheduleResponseDTO createSchedule(AdminScheduleRequestDTO.Create request) {
		Study study = studyRepository.findById(request.getStudyId())
			.orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

		// Section ID 리스트를 이용해 실제 Section 엔티티 리스트를 조회
		List<Section> sections = sectionRepository.findAllById(request.getSectionIds());

		// Schedule 엔티티 생성
		Schedule schedule = Schedule.create(
			study,
			request.getName(),
			request.getDescription(),
			request.getScheduleDate(),
			request.getMeetingLocation(),
			sections
		);
		Schedule saved = scheduleRepository.save(schedule);

		return AdminScheduleResponseDTO.of(
			saved.getId(),
			saved.getStudy().getId(),
			saved.getName(),
			saved.getDescription(),
			saved.getScheduleDate(),
			saved.getMeetingLocation(),
			saved.getSections().stream().map(Section::getId).collect(Collectors.toList()),
			saved.getCreatedAt(),
			saved.getUpdatedAt()
		);
	}

	@Transactional(readOnly = true)
	public List<AdminScheduleResponseDTO> getAllSchedules() {
		List<Schedule> schedules = scheduleRepository.findAll();
		return schedules.stream()
			.map(schedule -> AdminScheduleResponseDTO.of(
				schedule.getId(),
				schedule.getStudy().getId(),
				schedule.getName(),
				schedule.getDescription(),
				schedule.getScheduleDate(),
				schedule.getMeetingLocation(),
				schedule.getSections().stream().map(Section::getId).collect(Collectors.toList()),
				schedule.getCreatedAt(),
				schedule.getUpdatedAt()
			))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public AdminScheduleResponseDTO getSchedule(Long id) {
		Schedule schedule = scheduleRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));
		return AdminScheduleResponseDTO.of(
			schedule.getId(),
			schedule.getStudy().getId(),
			schedule.getName(),
			schedule.getDescription(),
			schedule.getScheduleDate(),
			schedule.getMeetingLocation(),
			schedule.getSections().stream().map(Section::getId).collect(Collectors.toList()),
			schedule.getCreatedAt(),
			schedule.getUpdatedAt()
		);
	}

	@Transactional
	public AdminScheduleResponseDTO updateSchedule(Long id, AdminScheduleRequestDTO.Update request) {
		Schedule schedule = scheduleRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

		List<Section> sections = sectionRepository.findAllById(request.getSectionIds());

		schedule.update(
			request.getName(),
			request.getDescription(),
			request.getScheduleDate(),
			request.getMeetingLocation(),
			sections
		);

		return AdminScheduleResponseDTO.of(
			schedule.getId(),
			schedule.getStudy().getId(),
			schedule.getName(),
			schedule.getDescription(),
			schedule.getScheduleDate(),
			schedule.getMeetingLocation(),
			schedule.getSections().stream().map(Section::getId).collect(Collectors.toList()),
			schedule.getCreatedAt(),
			schedule.getUpdatedAt()
		);
	}

	@Transactional
	public AdminScheduleDeleteResponseDTO deleteSchedule(Long id) {
		Schedule schedule = scheduleRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));
		String name = schedule.getName();
		scheduleRepository.delete(schedule);
		return AdminScheduleDeleteResponseDTO.of(name);
	}
}