package io.github.tcmytt.ecommerce.domain.request;

import io.github.tcmytt.ecommerce.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateOrderStatusDTO {
    private OrderStatus status;
}