package io.github.tcmytt.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.tcmytt.ecommerce.domain.request.ReqCreateReviewDTO;
import io.github.tcmytt.ecommerce.domain.response.ResReviewResponseDTO;
import io.github.tcmytt.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create a review", description = "Create a new review for a product")
    @ApiResponse(responseCode = "201", description = "Review created successfully")
    @PostMapping
    public ResponseEntity<ResReviewResponseDTO> createReview(
            @RequestBody ReqCreateReviewDTO dto,
            @RequestHeader("userId") Long userId) {
        ResReviewResponseDTO response = reviewService.createReview(dto, userId);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get reviews by product", description = "Get all reviews for a specific product")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ResReviewResponseDTO>> getReviewsByProduct(@PathVariable Long productId) {
        List<ResReviewResponseDTO> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    // get Reviews by Id
    @Operation(summary = "Get review by ID", description = "Get a review by its ID")
    @ApiResponse(responseCode = "200", description = "Review retrieved successfully")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResReviewResponseDTO> getReviewById(@PathVariable Long reviewId) {
        ResReviewResponseDTO response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a review", description = "Update an existing review")
    @ApiResponse(responseCode = "200", description = "Review updated successfully")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReqCreateReviewDTO dto) {
        ResReviewResponseDTO response = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a review", description = "Delete an existing review")
    @ApiResponse(responseCode = "204", description = "Review deleted successfully")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
