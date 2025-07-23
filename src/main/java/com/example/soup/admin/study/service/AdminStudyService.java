package com.example.soup.admin.study.service;

import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.example.soup.admin.study.dto.AdminStudyResponseDTO;
import com.example.soup.admin.study.dto.AdminStudyDeleteResponseDTO;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStudyService {

	private final StudyRepository studyRepository;

	@Transactional
	public AdminStudyResponseDTO createStudy(AdminStudyRequestDTO.Create request) {
		Study study = Study.create(
			request.getName(),
			request.getDescription(),
			request.getType(),
			request.getPeriod()
		);
		Study saved = studyRepository.save(study);
		return AdminStudyResponseDTO.of(
			saved.getName(),
			saved.getDescription(),
			saved.getType(),
			saved.getPeriod(),
			saved.getIsCompleted(),
			saved.getCreatedAt()
		);
	}

	@Transactional
	public AdminStudyResponseDTO updateStudy(Long id, AdminStudyRequestDTO.Update request) {
		Study study = studyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

		study.update(
			request.getName(),
			request.getDescription(),
			request.getType(),
			request.getPeriod(),
			request.getIsCompleted()
		);

		return AdminStudyResponseDTO.of(
			study.getName(),
			study.getDescription(),
			study.getType(),
			study.getPeriod(),
			study.getIsCompleted(),
			study.getCreatedAt()
		);
	}

	@Transactional(readOnly = true)
	public AdminStudyResponseDTO getStudy(Long id) {
		Study study = studyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

		return AdminStudyResponseDTO.of(
			study.getName(),
			study.getDescription(),
			study.getType(),
			study.getPeriod(),
			study.getIsCompleted(),
			study.getCreatedAt()
		);
	}

	@Transactional
	public AdminStudyDeleteResponseDTO deleteStudy(Long id) {
		Study study = studyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 스터디가 존재하지 않습니다."));

		String name = study.getName();
		studyRepository.delete(study);

		return AdminStudyDeleteResponseDTO.of(name);
	}
}
