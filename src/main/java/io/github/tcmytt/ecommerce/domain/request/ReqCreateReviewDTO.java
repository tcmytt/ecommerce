package io.github.tcmytt.ecommerce.domain.request;

import lombok.Data;

@Data
public class ReqCreateReviewDTO {
    private Long productId;
    private Integer rating;
    private String title;
    private String content;
}