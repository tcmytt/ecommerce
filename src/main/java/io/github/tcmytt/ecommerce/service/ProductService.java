package io.github.tcmytt.ecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Product;
import io.github.tcmytt.ecommerce.domain.ProductImage;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateProductDTO;
import io.github.tcmytt.ecommerce.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    public ProductService(ProductRepository productRepository, ProductImageService productImageService) {
        this.productImageService = productImageService;
        this.productRepository = productRepository;
    }

    public Page<Product> fetchAllWithPaginationAndSorting(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public Product createProduct(ReqCreateProductDTO productDTO, List<String> imagePaths, User user) {

        // Tạo sản phẩm
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        // product.setCategory(categoryService.findById(productDTO.getCategoryId()));
        product.setUser(user);
        product.setActive(true);

        Product savedProduct = this.productRepository.save(product);

        // Lưu ảnh và đánh dấu ảnh chính
        for (int i = 0; i < imagePaths.size(); i++) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setImage(imagePaths.get(i));
            productImage.setIsMain(i == productDTO.getMainImageIndex());
            productImageService.save(productImage);
        }
        product.setMainImage(imagePaths.get(productDTO.getMainImageIndex()));
        this.productRepository.save(product);

        return savedProduct;
    }

}
