package io.github.tcmytt.ecommerce.service;

import io.github.tcmytt.ecommerce.domain.Product;
import io.github.tcmytt.ecommerce.domain.Review;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateReviewDTO;
import io.github.tcmytt.ecommerce.domain.response.ResReviewResponseDTO;
import io.github.tcmytt.ecommerce.repository.ProductRepository;
import io.github.tcmytt.ecommerce.repository.ReviewRepository;
import io.github.tcmytt.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // getReviewById
    public ResReviewResponseDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));
        return new ResReviewResponseDTO(review);
    }

    // Tạo review mới
    public ResReviewResponseDTO createReview(ReqCreateReviewDTO dto, Long userId) {
        // Kiểm tra sản phẩm tồn tại
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tạo review
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(dto.getRating());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());

        // Lưu review vào database
        Review savedReview = reviewRepository.save(review);

        // Trả về DTO
        return new ResReviewResponseDTO(savedReview);
    }

    // Lấy danh sách review theo sản phẩm
    public List<ResReviewResponseDTO> getReviewsByProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(ResReviewResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Cập nhật review
    public ResReviewResponseDTO updateReview(Long reviewId, ReqCreateReviewDTO dto) {
        // Tìm review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));

        // Cập nhật thông tin
        review.setRating(dto.getRating());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());

        // Lưu lại vào database
        Review updatedReview = reviewRepository.save(review);

        // Trả về DTO
        return new ResReviewResponseDTO(updatedReview);
    }

    // Xóa review
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}