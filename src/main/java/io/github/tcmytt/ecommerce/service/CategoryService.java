package io.github.tcmytt.ecommerce.service;

import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Category;
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

}
