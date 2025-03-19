package io.github.tcmytt.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

import io.github.tcmytt.ecommerce.domain.enums.CouponType;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    // Constructor đầy đủ tham số
    public Coupon(CouponType type, String code, BigDecimal value, String startDate, String endDate,
            BigDecimal minSpend, BigDecimal maxSpend, Integer usesPerUser, Integer usesPerCoupon, Boolean status) {
        this.type = type;
        this.code = code;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minSpend = minSpend;
        this.maxSpend = maxSpend;
        this.usesPerUser = usesPerUser;
        this.usesPerCoupon = usesPerCoupon;
        this.status = status;
    }
}
