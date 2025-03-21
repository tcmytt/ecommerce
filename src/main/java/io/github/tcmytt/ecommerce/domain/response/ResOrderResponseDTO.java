package io.github.tcmytt.ecommerce.domain.response;

import io.github.tcmytt.ecommerce.domain.Order;
import io.github.tcmytt.ecommerce.domain.OrderDetail;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ResOrderResponseDTO {
    private Long id;
    private String status;
    private BigDecimal shippingFee;
    private String couponCode;
    private BigDecimal total;
    private String createdAt;
    private List<ResOrderDetailDTO> orderDetails;

    public ResOrderResponseDTO(Order order) {
        this.id = order.getId();
        this.status = order.getStatus().name();
        this.shippingFee = order.getShippingFee();
        this.couponCode = order.getCoupon() != null ? order.getCoupon().getCode() : null;
        this.total = order.getTotal();
        this.createdAt = order.getCreatedAt();
        this.orderDetails = order.getOrderDetails().stream()
                .map(ResOrderDetailDTO::new)
                .toList();
    }

    @Data
    public static class ResOrderDetailDTO {
        private Long id;
        private Long productId;
        private Integer quantity;

        public ResOrderDetailDTO(OrderDetail orderDetail) {
            this.id = orderDetail.getId();
            this.productId = orderDetail.getProduct().getId();
            this.quantity = orderDetail.getQuantity();
        }
    }
}