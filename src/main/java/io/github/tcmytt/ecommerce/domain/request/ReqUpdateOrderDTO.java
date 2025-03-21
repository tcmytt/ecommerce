package io.github.tcmytt.ecommerce.domain.request;

import java.util.List;
import lombok.Data;

@Data
public class ReqUpdateOrderDTO {
    private List<OrderDetailDTO> orderDetails; // Chi tiết đơn hàng cần cập nhật

    @Data
    public static class OrderDetailDTO {
        private Long orderDetailId; // ID của chi tiết đơn hàng cần cập nhật
        private Integer quantity; // Số lượng mới
    }
}