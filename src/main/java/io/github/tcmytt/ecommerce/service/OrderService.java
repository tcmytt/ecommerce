package io.github.tcmytt.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.tcmytt.ecommerce.domain.Coupon;
import io.github.tcmytt.ecommerce.domain.Order;
import io.github.tcmytt.ecommerce.domain.OrderDetail;
import io.github.tcmytt.ecommerce.domain.Product;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.enums.OrderStatus;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateOrderDTO;
import io.github.tcmytt.ecommerce.repository.CouponRepository;
import io.github.tcmytt.ecommerce.repository.OrderRepository;
import io.github.tcmytt.ecommerce.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
            CouponRepository couponRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
    }

    public Order createOrder(ReqCreateOrderDTO orderDTO, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        // Tính tổng tiền hàng
        BigDecimal subtotal = calculateSubtotal(orderDTO.getOrderDetails());
        order.setShippingFee(calculateShippingFee(subtotal)); // Tính phí vận chuyển

        // Áp dụng mã giảm giá (nếu có)
        if (orderDTO.getCouponCode() != null) {
            Coupon coupon = applyCoupon(orderDTO.getCouponCode(), subtotal);
            order.setCoupon(coupon);
        }

        // Tính tổng tiền sau khi áp dụng mã giảm giá
        BigDecimal total = calculateTotal(subtotal, order.getShippingFee(), order.getCoupon());

        // Lưu chi tiết đơn hàng
        List<OrderDetail> orderDetails = saveOrderDetails(order, orderDTO.getOrderDetails());
        order.setOrderDetails(orderDetails);
        order.setTotal(total);

        return orderRepository.save(order);
    }

    /**
     * Tính tổng tiền hàng
     */
    private BigDecimal calculateSubtotal(List<ReqCreateOrderDTO.OrderDetailDTO> orderDetails) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ReqCreateOrderDTO.OrderDetailDTO detailDTO : orderDetails) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));
        }
        return subtotal;
    }

    /**
     * Tính phí vận chuyển
     */
    private BigDecimal calculateShippingFee(BigDecimal subtotal) {
        // Ví dụ: Phí vận chuyển cố định 10.00 nếu tổng tiền hàng < 200.00, miễn phí nếu
        // >= 200.00
        return subtotal.compareTo(BigDecimal.valueOf(200.00)) < 0
                ? BigDecimal.valueOf(10.00)
                : BigDecimal.ZERO;
    }

    /**
     * Áp dụng mã giảm giá
     */
    private Coupon applyCoupon(String couponCode, BigDecimal subtotal) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Kiểm tra điều kiện sử dụng mã giảm giá
        if (coupon.getMinSpend() != null && subtotal.compareTo(coupon.getMinSpend()) < 0) {
            throw new RuntimeException("Minimum spend requirement not met");
        }
        if (coupon.getMaxSpend() != null && subtotal.compareTo(coupon.getMaxSpend()) > 0) {
            throw new RuntimeException("Maximum spend limit exceeded");
        }
        if (!coupon.getStatus()) {
            throw new RuntimeException("Coupon is inactive");
        }

        return coupon;
    }

    /**
     * Tính tổng tiền cuối cùng
     */
    private BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal shippingFee, Coupon coupon) {
        BigDecimal total = subtotal.add(shippingFee);

        // Áp dụng giảm giá nếu có mã giảm giá
        if (coupon != null) {
            switch (coupon.getType()) {
                case PERCENTAGE:
                    total = total.subtract(total.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100)));
                    break;
                case FIXED:
                    total = total.subtract(coupon.getValue());
                    break;
            }
        }

        return total.max(BigDecimal.ZERO); // Đảm bảo tổng tiền không âm
    }

    /**
     * Lưu chi tiết đơn hàng
     */
    private List<OrderDetail> saveOrderDetails(Order order, List<ReqCreateOrderDTO.OrderDetailDTO> orderDetails) {
        List<OrderDetail> details = new ArrayList<>();
        for (ReqCreateOrderDTO.OrderDetailDTO detailDTO : orderDetails) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailDTO.getQuantity());
            details.add(orderDetail);
        }
        return details;
    }

    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    // Lấy đơn hàng theo ID và kiểm tra quyền sở hữu của người dùng
    public Order getOrderByIdAndUser(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // Cập nhật trạng thái đơn hàng
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // Hủy đơn hàng
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("Cannot cancel an order that is not PENDING");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
