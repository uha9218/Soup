package com.example.soup.admin.section.service;

import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import com.example.soup.section.entity.Section;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;
import com.example.soup.section.repository.SectionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSectionService {

	private final SectionRepository sectionRepository;
	private final StudyRepository studyRepository;

	@Transactional
	public AdminSectionResponseDTO createSection(AdminSectionRequestDTO request) {
		Study study = studyRepository.findById(request.getStudyId())
			.orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

		Section section = Section.create(
			request.getSectionNumber(),
			request.getSectionName(),
			study
		);

		Section saved = sectionRepository.save(section);

		return AdminSectionResponseDTO.of(
			saved.getSectionNumber(),
			saved.getSectionName(),
			saved.getStudy().getId()
		);
	}

	@Transactional(readOnly = true)
	public AdminSectionResponseDTO getSection(Long id) {
		Section section = sectionRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 섹션이 존재하지 않습니다."));

		return AdminSectionResponseDTO.of(
			section.getSectionNumber(),
			section.getSectionName(),
			section.getStudy().getId()
		);
	}

	@Transactional
	public AdminSectionResponseDTO updateSection(Long id, AdminSectionRequestDTO request) {
		Section section = sectionRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 섹션이 존재하지 않습니다."));

		section.update(request.getSectionNumber(), request.getSectionName());

		return AdminSectionResponseDTO.of(
			section.getSectionNumber(),
			section.getSectionName(),
			section.getStudy().getId()
		);
	}

	@Transactional
	public AdminSectionDeleteResponseDTO deleteSection(Long id) {
		Section section = sectionRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 섹션이 존재하지 않습니다."));

		sectionRepository.delete(section);

		return AdminSectionDeleteResponseDTO.of(
			section.getSectionNumber(),
			section.getSectionName()
		);
	}
}
