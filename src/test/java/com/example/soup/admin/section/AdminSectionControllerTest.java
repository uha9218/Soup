package com.example.soup.admin.section;

import com.example.soup.admin.section.controller.AdminSectionController;
import com.example.soup.admin.section.dto.AdminSectionRequestDTO;
import com.example.soup.admin.section.dto.AdminSectionResponseDTO;
import com.example.soup.admin.section.dto.AdminSectionDeleteResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminSectionController.class)
class AdminSectionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("섹션 생성 API - 성공")
	void createSection_success() throws Exception {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(1L)
			.sectionName("섹션1")
			.studyId(100L)
			.build();

		mockMvc.perform(post("/admin/section")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sectionNumber").value(1L))
			.andExpect(jsonPath("$.sectionName").value("섹션1"));
	}

	@Test
	@DisplayName("섹션 수정 API - 성공")
	void updateSection_success() throws Exception {
		AdminSectionRequestDTO request = AdminSectionRequestDTO.builder()
			.sectionNumber(1L)
			.sectionName("수정된 섹션")
			.studyId(100L)
			.build();

		mockMvc.perform(put("/admin/section")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sectionName").value("수정된 섹션"));
	}

	@Test
	@DisplayName("섹션 조회 API - 성공")
	void getSection_success() throws Exception {
		mockMvc.perform(get("/admin/section"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sectionNumber").value(1L))
			.andExpect(jsonPath("$.sectionName").value("섹션 이름"));
	}

	@Test
	@DisplayName("섹션 삭제 API - 성공")
	void deleteSection_success() throws Exception {
		mockMvc.perform(delete("/admin/section")
				.param("sectionNumber", "1")
				.param("sectionName", "섹션1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.sectionNumber").value(1L))
			.andExpect(jsonPath("$.sectionName").value("섹션1"));
	}
}
