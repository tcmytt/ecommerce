package io.github.tcmytt.ecommerce.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
    private Integer mainImageIndex; // Chỉ số của ảnh chính (từ 0)
}