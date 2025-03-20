package io.github.tcmytt.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.tcmytt.ecommerce.domain.request.ReqCreateCategoryDTO;
import io.github.tcmytt.ecommerce.domain.response.ResCategoryResponseDTO;
import io.github.tcmytt.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a category", description = "Create a new category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @PostMapping
    public ResponseEntity<ResCategoryResponseDTO> createCategory(@RequestBody ReqCreateCategoryDTO dto) {
        ResCategoryResponseDTO response = categoryService.createCategory(dto);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get all categories hierarchically", description = "Get all categories organized by hierarchy")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ResCategoryResponseDTO>> getAllCategoriesHierarchically() {
        return ResponseEntity.ok(categoryService.getAllCategoriesHierarchically());
    }

    @Operation(summary = "Get category by ID", description = "Get a category by its ID")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    @GetMapping("/{categoryId}")
    public ResponseEntity<ResCategoryResponseDTO> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @Operation(summary = "Update a category", description = "Update an existing category")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ResCategoryResponseDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody ReqCreateCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, dto));
    }

    @Operation(summary = "Delete a category", description = "Delete an existing category")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
