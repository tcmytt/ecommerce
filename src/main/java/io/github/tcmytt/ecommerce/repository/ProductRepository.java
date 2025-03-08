package io.github.tcmytt.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}