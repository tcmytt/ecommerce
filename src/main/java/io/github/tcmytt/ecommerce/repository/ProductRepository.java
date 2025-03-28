package io.github.tcmytt.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm sản phẩm theo categoryId với phân trang và sắp xếp
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Lọc sản phẩm theo categoryId và search
    Page<Product> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String search, Pageable pageable);

    // Tìm kiếm sản phẩm theo từ khóa
    Page<Product> findByNameContainingIgnoreCase(String search, Pageable pageable);

}