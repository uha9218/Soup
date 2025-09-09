package com.example.soup.admin.section.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSectionService {

	private final SectionRepository sectionRepository;
	private final StudyRepository studyRepository;

	// 1. 생성(Create)
	@Transactional
	public AdminSectionResponseDTO createSection(AdminSectionRequestDTO.Create request) {
		Study study = studyRepository.findById(request.getStudyId())
			.orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

		Section section = Section.create(
			request.getSectionNumber(),
			request.getSectionName(),
			study,
			null,
			true // 기본적으로 회고 필요
		);

		Section saved = sectionRepository.save(section);

		return AdminSectionResponseDTO.of(
			saved.getSectionNumber(),
			saved.getSectionName(),
			saved.getStudy().getId()
		);
	}

	// 2. 전체 조회(Read All)
	@Transactional(readOnly = true)
	public List<AdminSectionResponseDTO> getAllSections() {
		return sectionRepository.findAll().stream()
			.map(section -> AdminSectionResponseDTO.of(
				section.getSectionNumber(),
				section.getSectionName(),
				section.getStudy().getId()
			))
			.toList();
	}

	// 3. 단건 조회(Read)
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

	// 4. 수정(Update)
	@Transactional
	public AdminSectionResponseDTO updateSection(Long id, AdminSectionRequestDTO.Update request) {
		Section section = sectionRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 섹션이 존재하지 않습니다."));

		section.update(request.getSectionNumber(), request.getSectionName(), true);

		return AdminSectionResponseDTO.of(
			section.getSectionNumber(),
			section.getSectionName(),
			section.getStudy().getId()
		);
	}

	// 5. 삭제(Delete)
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
