package com.example.soup.admin.section;

import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import com.example.soup.admin.section.service.AdminSectionService;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.study.entity.Study;
import com.example.soup.study.repository.StudyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminSectionServiceUnitTest {

	private SectionRepository sectionRepository;
	private StudyRepository studyRepository;
	private AdminSectionService sectionService;

	@BeforeEach
	void setUp() {
		sectionRepository = mock(SectionRepository.class);
		studyRepository = mock(StudyRepository.class);
		sectionService = new AdminSectionService(sectionRepository, studyRepository);
	}

	@Test
	@DisplayName("섹션 생성 성공")
	void createSection_success() {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(1L)
			.sectionName("섹션1")
			.studyId(100L)
			.build();

		Study dummyStudy = Study.create("스터디", "설명", "온라인", "4개월");
		Section dummySection = Section.create(1L, "섹션1", dummyStudy);

		when(studyRepository.findById(100L)).thenReturn(Optional.of(dummyStudy));
		when(sectionRepository.save(any(Section.class))).thenReturn(dummySection);

		AdminSectionResponseDTO response = sectionService.createSection(request);

		assertThat(response).isNotNull();
		assertThat(response.getSectionNumber()).isEqualTo(1L);
		assertThat(response.getSectionName()).isEqualTo("섹션1");
	}

	@Test
	@DisplayName("섹션 조회 실패 - 존재하지 않음")
	void getSection_notFound() {
		when(sectionRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> sectionService.getSection(1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 섹션이 존재하지 않습니다.");
	}

	@Test
	@DisplayName("섹션 수정 성공")
	void updateSection_success() {
		Study study = Study.create("스터디", "설명", "온라인", "4개월");
		Section section = Section.create(1L, "초기 섹션", study);

		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(2L)
			.sectionName("수정된 섹션")
			.studyId(100L)
			.build();

		when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));

		AdminSectionResponseDTO response = sectionService.updateSection(1L, request);

		assertThat(response.getSectionNumber()).isEqualTo(2L);
		assertThat(response.getSectionName()).isEqualTo("수정된 섹션");
	}

	@Test
	@DisplayName("섹션 삭제 성공")
	void deleteSection_success() {
		Study study = Study.create("스터디", "설명", "온라인", "4개월");
		Section section = Section.create(1L, "삭제 섹션", study);

		when(sectionRepository.findById(1L)).thenReturn(Optional.of(section));

		AdminSectionDeleteResponseDTO response = sectionService.deleteSection(1L);

		assertThat(response.getSectionName()).isEqualTo("삭제 섹션");
		verify(sectionRepository, times(1)).delete(section);
	}
}

