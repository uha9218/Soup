package com.example.soup.admin.schedule;

import com.example.soup.admin.schedule.controller.AdminScheduleController;
import com.example.soup.admin.schedule.dto.AdminScheduleRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminScheduleController.class)
class AdminScheduleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("일정 생성 API - 성공")
	void createSchedule_success() throws Exception {
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("스터디 일정")
			.type("온라인")
			.studyId(100L)
			.sectionIds(List.of(1L, 2L))
			.meetingLink("https://zoom.us/example")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		mockMvc.perform(post("/admin/schedule")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("스터디 일정"));
	}

	@Test
	@DisplayName("일정 수정 API - 성공")
	void updateSchedule_success() throws Exception {
		AdminScheduleRequestDTO request = AdminScheduleRequestDTO.builder()
			.title("수정된 일정")
			.type("오프라인")
			.studyId(100L)
			.sectionIds(List.of(3L, 4L))
			.meetingLink("https://maps.example.com/location")
			.scheduleDateTime(LocalDateTime.now())
			.build();

		mockMvc.perform(put("/admin/schedule")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("수정된 일정"));
	}

	@Test
	@DisplayName("일정 조회 API - 성공")
	void getSchedule_success() throws Exception {
		mockMvc.perform(get("/admin/schedule"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("더미 일정"));
	}

	@Test
	@DisplayName("일정 삭제 API - 성공")
	void deleteSchedule_success() throws Exception {
		mockMvc.perform(delete("/admin/schedule")
				.param("title", "삭제할 일정"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("삭제할 일정"));
	}
}
