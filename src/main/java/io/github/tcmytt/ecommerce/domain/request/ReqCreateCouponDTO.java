package io.github.tcmytt.ecommerce.domain.request;

import io.github.tcmytt.ecommerce.domain.enums.CouponType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqCreateCouponDTO {
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
}