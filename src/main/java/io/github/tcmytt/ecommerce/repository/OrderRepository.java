package io.github.tcmytt.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
