package io.github.tcmytt.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Order;
import io.github.tcmytt.ecommerce.domain.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Tìm đơn hàng theo orderId và userId
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    Page<Order> findByUserIdAndStatusNot(Long userId, String status, Pageable pageable);
}
