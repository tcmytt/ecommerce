package io.github.tcmytt.ecommerce.domain.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateOrderDTO {
    private String couponCode;
    private List<OrderDetailDTO> orderDetails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class OrderDetailDTO {
        private Long productId;
        private Integer quantity;
    }

}
