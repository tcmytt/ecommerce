package io.github.tcmytt.ecommerce.domain.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResCategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private List<ResCategoryResponseDTO> children;

    public ResCategoryResponseDTO(Long id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.children = new ArrayList<>();
    }
}