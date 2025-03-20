package io.github.tcmytt.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Category;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateCategoryDTO;
import io.github.tcmytt.ecommerce.domain.response.ResCategoryResponseDTO;
import io.github.tcmytt.ecommerce.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findById(long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    // Tạo danh mục mới
    public ResCategoryResponseDTO createCategory(ReqCreateCategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToHierarchyDTO(savedCategory);
    }

    // Lấy danh sách danh mục theo cấp bậc
    public List<ResCategoryResponseDTO> getAllCategoriesHierarchically() {
        List<Category> rootCategories = categoryRepository.findByParentId(null);
        return rootCategories.stream()
                .map(this::convertToHierarchyDTO)
                .collect(Collectors.toList());
    }

    // Lấy danh mục theo ID
    public ResCategoryResponseDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        return convertToHierarchyDTO(category);
    }

    // Cập nhật danh mục
    public ResCategoryResponseDTO updateCategory(Long categoryId, ReqCreateCategoryDTO dto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParent(parent);
        } else {
            category.setParent(null); // Nếu không có danh mục cha
        }

        Category updatedCategory = categoryRepository.save(category);
        return convertToHierarchyDTO(updatedCategory);
    }

    // Xóa danh mục
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    // Chuyển đổi Category thành CategoryHierarchyDTO(ResCategoryResponseDTO)
    private ResCategoryResponseDTO convertToHierarchyDTO(Category category) {
        ResCategoryResponseDTO dto = new ResCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl());
        List<Category> children = categoryRepository.findByParentId(category.getId());
        if (!children.isEmpty()) {
            dto.setChildren(children.stream()
                    .map(this::convertToHierarchyDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

}
