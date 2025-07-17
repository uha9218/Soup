package com.example.soup.admin.study;

import com.example.soup.admin.study.controller.AdminStudyController;
import com.example.soup.admin.study.dto.AdminStudyRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminStudyController.class)
class AdminStudyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("스터디 생성 API - 성공")
	void createStudy_success() throws Exception {
		AdminStudyRequestDTO.Create request = AdminStudyRequestDTO.Create.builder()
			.name("Spring Study")
			.description("Spring Boot Mastery")
			.type("온라인")
			.period("4 months")
			.build();

		mockMvc.perform(post("/admin/study")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Spring Study"));
	}

	@Test
	@DisplayName("스터디 수정 API - 성공")
	void updateStudy_success() throws Exception {
		AdminStudyRequestDTO.Update request = AdminStudyRequestDTO.Update.builder()
			.name("Updated Study")
			.description("Updated Description")
			.type("오프라인")
			.period("2 months")
			.isCompleted(true)
			.build();

		mockMvc.perform(put("/admin/study")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Updated Study"))
			.andExpect(jsonPath("$.isCompleted").value(true));
	}

	@Test
	@DisplayName("스터디 조회 API - 성공")
	void getStudy_success() throws Exception {
		mockMvc.perform(get("/admin/study"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Dummy Study"));
	}

	@Test
	@DisplayName("스터디 삭제 API - 성공")
	void deleteStudy_success() throws Exception {
		mockMvc.perform(delete("/admin/study")
				.param("name", "Spring Study"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Spring Study"));
	}
}
