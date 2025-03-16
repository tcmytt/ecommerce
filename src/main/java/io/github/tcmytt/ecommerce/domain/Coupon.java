package io.github.tcmytt.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import io.github.tcmytt.ecommerce.domain.enums.CouponType;

@Entity
@Table(name = "coupons")
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    private String code;

    private BigDecimal value;

    private String startDate;

    private String endDate;

    private BigDecimal minSpend;

    private BigDecimal maxSpend;

    private Integer usesPerUser;

    private Integer usesPerCoupon;

    private Boolean status;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
}
