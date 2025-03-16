package io.github.tcmytt.ecommerce.domain.enums;

public enum OrderStatus {
    PENDING, // Chờ xử lý
    SHIPPED, // Đã giao hàng
    DELIVERED, // Đã nhận hàng
    CANCELLED // Đã hủy
}