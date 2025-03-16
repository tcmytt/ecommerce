package io.github.tcmytt.ecommerce.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.tcmytt.ecommerce.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(precision = 10, scale = 2) // DECIMAL(10, 2) cho phí vận chuyển
    private BigDecimal shippingFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(precision = 10, scale = 2) // DECIMAL(10, 2) cho tổng tiền
    private BigDecimal total;

    private String createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Tránh tuần hoàn khi serialize JSON
    private List<OrderDetail> orderDetails;

    // Callback trước khi persist
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now().toString();
    }

}
