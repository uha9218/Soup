package com.example.soup.review.service;

import java.util.List;

import com.example.soup.review.dto.ReviewRequestDTO;
import com.example.soup.review.dto.ReviewResponseDTO;
import com.example.soup.review.dto.ReviewDeleteResponseDTO;
import com.example.soup.review.entity.Review;
import com.example.soup.review.repository.ReviewRepository;
import com.example.soup.section.entity.Section;
import com.example.soup.section.repository.SectionRepository;
import com.example.soup.user.entity.User;
import com.example.soup.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final SectionRepository sectionRepository;

	/** 회고 생성 */
	@Transactional
	public ReviewResponseDTO createReview(ReviewRequestDTO.Create request) {
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
		Section section = sectionRepository.findById(request.getSectionId())
			.orElseThrow(() -> new IllegalArgumentException("해당 섹션이 존재하지 않습니다."));

		Review review = Review.create(
			user,
			section,
			request.getContent(),
			request.getReviewUrl()
		);

		Review saved = reviewRepository.save(review);

		return ReviewResponseDTO.of(
			saved.getId(),
			saved.getUser().getId(),
			saved.getUser().getUsername(),
			saved.getSection().getId(),
			saved.getSection().getSectionName(),
			saved.getContent(),
			saved.getReviewUrl(),
			saved.getCreatedAt(),
			saved.getUpdatedAt()
		);
	}

	/** 회고 수정 */
	@Transactional
	public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO.Update request) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 회고가 존재하지 않습니다."));

		review.update(
			request.getContent(),
			request.getReviewUrl()
		);

		return ReviewResponseDTO.of(
			review.getId(),
			review.getUser().getId(),
			review.getUser().getUsername(),
			review.getSection().getId(),
			review.getSection().getSectionName(),
			review.getContent(),
			review.getReviewUrl(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	/** 회고 전체 조회 */
	@Transactional(readOnly = true)
	public List<ReviewResponseDTO> getAllReviews() {
		List<Review> reviews = reviewRepository.findAll();
		return reviews.stream()
			.map(review -> ReviewResponseDTO.of(
				review.getId(),
				review.getUser().getId(),
				review.getUser().getUsername(),
				review.getSection().getId(),
				review.getSection().getSectionName(),
				review.getContent(),
				review.getReviewUrl(),
				review.getCreatedAt(),
				review.getUpdatedAt()
			))
			.toList();
	}

	/** 특정 회고 단건 조회 */
	@Transactional(readOnly = true)
	public ReviewResponseDTO getReview(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 회고가 존재하지 않습니다."));
		return ReviewResponseDTO.of(
			review.getId(),
			review.getUser().getId(),
			review.getUser().getUsername(),
			review.getSection().getId(),
			review.getSection().getSectionName(),
			review.getContent(),
			review.getReviewUrl(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	/** 회고 삭제 */
	@Transactional
	public ReviewDeleteResponseDTO deleteReview(Long id) {
		Review review = reviewRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 회고가 존재하지 않습니다."));
		String sectionName = review.getSection().getSectionName();
		reviewRepository.delete(review);
		return ReviewDeleteResponseDTO.of(sectionName);
	}
}
