package com.example.soup.section.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionProgressRequestDTO {
    private Long sectionId;  // 섹션 ID
}
