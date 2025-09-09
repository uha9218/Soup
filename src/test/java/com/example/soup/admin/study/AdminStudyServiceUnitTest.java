package com.example.soup.admin.study;

import com.example.soup.admin.study.dto.AdminStudyDeleteResponseDTO;
import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.example.soup.admin.study.dto.AdminStudyResponseDTO;
import com.example.soup.admin.study.service.AdminStudyService;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

class AdminStudyServiceUnitTest {

	private StudyRepository studyRepository;
	private AdminStudyService studyService;

	@BeforeEach
	void setUp() {
		studyRepository = mock(StudyRepository.class);
		studyService = new AdminStudyService(studyRepository);
	}
	@Nested
	@DisplayName("createStudy() 테스트")
	class CreateStudy {

		@Test
		@DisplayName("스터디 생성 성공")
		void create_success() {
			// given
			LocalDateTime startDate = LocalDateTime.of(2025, 8, 1, 0, 0);
			LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

			AdminStudyRequestDTO.Create request = AdminStudyRequestDTO.Create.builder()
				.name("Spring")
				.description("Spring Boot")
				.type("온라인")
				.startDate(startDate)
				.endDate(endDate)
				.build();

			Study dummy = Study.create(
				request.getName(),
				request.getDescription(),
				request.getType(),
				request.getStartDate(),
				request.getEndDate()
			);

			when(studyRepository.save(any(Study.class))).thenReturn(dummy);

			// when
			AdminStudyResponseDTO response = studyService.createStudy(request);

			// then
			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo("Spring");
			assertThat(response.getStartDate()).isEqualTo(startDate);
			assertThat(response.getEndDate()).isEqualTo(endDate);
			verify(studyRepository, times(1)).save(any(Study.class));
		}
	}

	@Test
	@DisplayName("스터디 전체 조회 - 유닛 테스트 성공")
	void getAllStudies_unit_success() {
		// given
		LocalDateTime start1 = LocalDateTime.of(2025, 8, 1, 0, 0);
		LocalDateTime end1 = LocalDateTime.of(2025, 12, 31, 23, 59);
		LocalDateTime start2 = LocalDateTime.of(2025, 9, 1, 0, 0);
		LocalDateTime end2 = LocalDateTime.of(2025, 11, 30, 23, 59);

		Study study1 = Study.create("스터디1", "설명1", "온라인", start1, end1);
		Study study2 = Study.create("스터디2", "설명2", "오프라인", start2, end2);

		given(studyRepository.findAll()).willReturn(List.of(study1, study2));

		// when
		List<AdminStudyResponseDTO> result = studyService.getAllStudies();

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("스터디1");
		assertThat(result.get(0).getStartDate()).isEqualTo(start1);
		assertThat(result.get(1).getType()).isEqualTo("오프라인");
		assertThat(result.get(1).getEndDate()).isEqualTo(end2);
	}


	@Nested
	@DisplayName("getStudy() 테스트")
	class GetStudy {

		@Test
		@DisplayName("스터디 조회 성공")
		void get_success() {
			LocalDateTime startDate = LocalDateTime.of(2025, 8, 1, 0, 0);
			LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

			Study study = Study.create("Spring", "설명", "온라인", startDate, endDate);

			when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

			AdminStudyResponseDTO response = studyService.getStudy(1L);

			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo("Spring");
			assertThat(response.getStartDate()).isEqualTo(startDate);
		}

		@Test
		@DisplayName("스터디 조회 실패 - 존재하지 않음")
		void get_fail_notFound() {
			when(studyRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> studyService.getStudy(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("해당 스터디가 존재하지 않습니다.");
		}
	}

	@Nested
	@DisplayName("updateStudy() 테스트")
	class UpdateStudy {

		@Test
		@DisplayName("스터디 수정 성공")
		void update_success() {
			// given
			LocalDateTime startDate = LocalDateTime.of(2025, 8, 1, 0, 0);
			LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);
			LocalDateTime actualEndDate = LocalDateTime.of(2025, 12, 31, 23, 59);

			Study study = Study.create("Spring", "설명", "온라인", startDate, endDate);

			AdminStudyRequestDTO.Update request = AdminStudyRequestDTO.Update.builder()
				.name("Updated")
				.description("Updated Desc")
				.type("오프라인")
				.startDate(startDate)
				.endDate(endDate)
				.actualEndDate(actualEndDate)
				.completed(true)
				.isActive(true)
				.build();

			when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

			// when
			AdminStudyResponseDTO response = studyService.updateStudy(1L, request);

			// then
			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo("Updated");
			assertThat(response.getCompleted()).isTrue();
			assertThat(response.getActualEndDate()).isEqualTo(actualEndDate);
			verify(studyRepository, never()).save(any()); // save 호출 안 함 (영속성 컨텍스트 내 변경만)
		}

		@Test
		@DisplayName("스터디 수정 실패 - 존재하지 않음")
		void update_fail_notFound() {
			AdminStudyRequestDTO.Update request = AdminStudyRequestDTO.Update.builder()
				.name("Updated")
				.description("Updated Desc")
				.type("오프라인")
				.startDate(LocalDateTime.of(2025, 8, 1, 0, 0))
				.endDate(LocalDateTime.of(2025, 12, 31, 23, 59))
				.actualEndDate(LocalDateTime.of(2025, 12, 31, 23, 59))
				.completed(true)
				.isActive(true)
				.build();

			when(studyRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> studyService.updateStudy(1L, request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("해당 스터디가 존재하지 않습니다.");
		}
	}

	@Nested
	@DisplayName("deleteStudy() 테스트")
	class DeleteStudy {

		@Test
		@DisplayName("스터디 삭제 성공")
		void delete_success() {
			LocalDateTime startDate = LocalDateTime.of(2025, 8, 1, 0, 0);
			LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

			Study study = Study.create("Spring", "설명", "온라인", startDate, endDate);

			when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

			AdminStudyDeleteResponseDTO response = studyService.deleteStudy(1L);

			assertThat(response).isNotNull();
			assertThat(response.getName()).isEqualTo("Spring");
			verify(studyRepository).delete(study);
		}

		@Test
		@DisplayName("스터디 삭제 실패 - 존재하지 않음")
		void delete_fail_notFound() {
			when(studyRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> studyService.deleteStudy(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("해당 스터디가 존재하지 않습니다.");
		}
	}
}