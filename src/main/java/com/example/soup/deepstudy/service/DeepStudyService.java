package com.example.soup.deepstudy.service;

import com.example.soup.deepstudy.dto.DeepStudyRequestDTO;
import com.example.soup.deepstudy.dto.DeepStudyResponseDTO;
import com.example.soup.deepstudy.dto.DeepStudyDeleteResponseDTO;
import com.example.soup.deepstudy.entity.DeepStudy;
import com.example.soup.deepstudy.repository.DeepStudyRepository;
import com.example.soup.user.entity.User;
import com.example.soup.user.repository.UserRepository;
import com.example.soup.schedule.entity.Schedule;
import com.example.soup.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeepStudyService {

	private final DeepStudyRepository deepStudyRepository;
	private final UserRepository userRepository;
	private final ScheduleRepository scheduleRepository;

	// 생성
	@Transactional
	public DeepStudyResponseDTO createDeepStudy(DeepStudyRequestDTO.Create request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
		Schedule schedule = scheduleRepository.findById(request.getScheduleId())
			.orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

		DeepStudy deepStudy = DeepStudy.create(
			user,
			schedule,
			request.getTopicTitle(),
			request.getContentUrl()
		);
		DeepStudy saved = deepStudyRepository.save(deepStudy);

		return DeepStudyResponseDTO.of(
			saved.getId(),
			saved.getUser().getId(),
			saved.getUser().getUsername(),
			saved.getSchedule().getId(),
			saved.getSchedule().getName(),
			saved.getTopicTitle(),
			saved.getContentUrl(),
			saved.getCreatedAt(),
			saved.getUpdatedAt()
		);
	}

	// 전체 조회
	@Transactional(readOnly = true)
	public List<DeepStudyResponseDTO> getAllDeepStudies() {
		List<DeepStudy> list = deepStudyRepository.findAll();
		return list.stream()
			.map(ds -> DeepStudyResponseDTO.of(
				ds.getId(),
				ds.getUser().getId(),
				ds.getUser().getUsername(),
				ds.getSchedule().getId(),
				ds.getSchedule().getName(),
				ds.getTopicTitle(),
				ds.getContentUrl(),
				ds.getCreatedAt(),
				ds.getUpdatedAt()
			))
			.toList();
	}

	// 단건 조회
	@Transactional(readOnly = true)
	public DeepStudyResponseDTO getDeepStudy(Long id) {
		DeepStudy ds = deepStudyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 심화학습이 존재하지 않습니다."));
		return DeepStudyResponseDTO.of(
			ds.getId(),
			ds.getUser().getId(),
			ds.getUser().getUsername(),
			ds.getSchedule().getId(),
			ds.getSchedule().getName(),
			ds.getTopicTitle(),
			ds.getContentUrl(),
			ds.getCreatedAt(),
			ds.getUpdatedAt()
		);
	}

	// 수정
	@Transactional
	public DeepStudyResponseDTO updateDeepStudy(Long id, DeepStudyRequestDTO.Update request) {
		DeepStudy ds = deepStudyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 심화학습이 존재하지 않습니다."));

		ds.update(request.getTopicTitle(), request.getContentUrl());

		return DeepStudyResponseDTO.of(
			ds.getId(),
			ds.getUser().getId(),
			ds.getUser().getUsername(),
			ds.getSchedule().getId(),
			ds.getSchedule().getName(),
			ds.getTopicTitle(),
			ds.getContentUrl(),
			ds.getCreatedAt(),
			ds.getUpdatedAt()
		);
	}

	// 삭제
	@Transactional
	public DeepStudyDeleteResponseDTO deleteDeepStudy(Long id) {
		DeepStudy ds = deepStudyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 심화학습이 존재하지 않습니다."));

		String scheduleName = ds.getSchedule().getName();
		String topicTitle = ds.getTopicTitle();

		deepStudyRepository.delete(ds);

		return DeepStudyDeleteResponseDTO.of(scheduleName, topicTitle);
	}
}
