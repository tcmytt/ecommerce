package io.github.tcmytt.ecommerce.domain.request;

import lombok.Data;

@Data
public class ReqCreateCategoryDTO {
    private String name;
    private String description;
    private Long parentId; // ID của danh mục cha (nếu có)
    private String imageUrl;
}