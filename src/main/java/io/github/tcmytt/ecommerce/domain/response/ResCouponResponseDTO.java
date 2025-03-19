package io.github.tcmytt.ecommerce.domain.response;

import io.github.tcmytt.ecommerce.domain.Coupon;
import io.github.tcmytt.ecommerce.domain.enums.CouponType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResCouponResponseDTO {
    private Long id;
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

    public ResCouponResponseDTO(Coupon coupon) {
        this.id = coupon.getId();
        this.type = coupon.getType();
        this.code = coupon.getCode();
        this.value = coupon.getValue();
        this.startDate = coupon.getStartDate();
        this.endDate = coupon.getEndDate();
        this.minSpend = coupon.getMinSpend();
        this.maxSpend = coupon.getMaxSpend();
        this.usesPerUser = coupon.getUsesPerUser();
        this.usesPerCoupon = coupon.getUsesPerCoupon();
        this.status = coupon.getStatus();
    }
}