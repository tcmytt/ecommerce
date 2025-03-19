package io.github.tcmytt.ecommerce.domain.response;

import io.github.tcmytt.ecommerce.domain.Review;
import lombok.Data;

@Data
public class ResReviewResponseDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String title;
    private String content;
    private String createdAt;

    public ResReviewResponseDTO(Review review) {
        this.id = review.getId();
        this.productId = review.getProduct().getId();
        this.userId = review.getUser().getId();
        this.rating = review.getRating();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
    }
}