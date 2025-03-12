package io.github.tcmytt.ecommerce.service;

import io.github.tcmytt.ecommerce.domain.ProductImage;
import io.github.tcmytt.ecommerce.repository.ProductImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public ProductImageService(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    public void save(ProductImage productImage) {
        productImageRepository.save(productImage);
    }
}