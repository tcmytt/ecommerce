package io.github.tcmytt.ecommerce.service;

import io.github.tcmytt.ecommerce.domain.*;
import io.github.tcmytt.ecommerce.domain.enums.OrderStatus;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateOrderDTO;
import io.github.tcmytt.ecommerce.domain.response.ResOrderResponseDTO;
import io.github.tcmytt.ecommerce.repository.OrderRepository;
import io.github.tcmytt.ecommerce.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Tạo đơn hàng
    public ResOrderResponseDTO createOrder(ReqCreateOrderDTO dto, User user) {
        // Kiểm tra nếu người dùng đã có đơn hàng PENDING
        Optional<Order> pendingOrder = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PENDING);
        if (pendingOrder.isPresent()) {
            throw new RuntimeException("Bạn đã có một đơn hàng đang chờ xử lý");
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        // Tính tổng tiền hàng
        BigDecimal subtotal = calculateSubtotal(dto.getOrderDetails());
        order.setShippingFee(calculateShippingFee(subtotal));

        // Áp dụng mã giảm giá (nếu có)
        if (dto.getCouponCode() != null) {
            Coupon coupon = applyCoupon(dto.getCouponCode(), subtotal);
            order.setCoupon(coupon);
        }

        // Tính tổng tiền
        BigDecimal total = calculateTotal(subtotal, order.getShippingFee(), order.getCoupon());
        order.setTotal(total);

        // Lưu chi tiết đơn hàng
        List<OrderDetail> orderDetails = saveOrderDetails(order, dto.getOrderDetails());
        order.setOrderDetails(orderDetails);

        Order savedOrder = orderRepository.save(order);
        return new ResOrderResponseDTO(savedOrder);
    }

    // Cập nhật đơn hàng
    public ResOrderResponseDTO updateOrder(Long orderId, ReqCreateOrderDTO dto, User user) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Chỉ cho phép cập nhật nếu trạng thái là PENDING
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("Không thể cập nhật đơn hàng khi trạng thái không phải PENDING");
        }

        // Kiểm tra quyền sở hữu đơn hàng
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật đơn hàng này");
        }

        // Cập nhật chi tiết đơn hàng
        List<OrderDetail> updatedOrderDetails = saveOrderDetails(order, dto.getOrderDetails());
        order.setOrderDetails(updatedOrderDetails);

        // Tính lại tổng tiền
        BigDecimal subtotal = calculateSubtotalFromOrderDetails(order.getOrderDetails());
        order.setShippingFee(calculateShippingFee(subtotal));
        order.setTotal(calculateTotal(subtotal, order.getShippingFee(), order.getCoupon()));

        Order updatedOrder = orderRepository.save(order);
        return new ResOrderResponseDTO(updatedOrder);
    }

    // Thay đổi trạng thái đơn hàng
    public ResOrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Chỉ cho phép chuyển trạng thái hợp lệ
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException("Không thể chuyển trạng thái từ " + order.getStatus() + " sang " + newStatus);
        }

        // Cập nhật trạng thái
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return new ResOrderResponseDTO(updatedOrder);
    }

    // Lấy đơn hàng hiện tại của người dùng
    public ResOrderResponseDTO getCurrentOrder(User user) {
        // Tìm đơn hàng PENDING của người dùng
        Optional<Order> pendingOrder = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PENDING);
        if (pendingOrder.isEmpty()) {
            throw new RuntimeException("Bạn không có đơn hàng nào đang chờ xử lý");
        }
        return new ResOrderResponseDTO(pendingOrder.get());
    }

    // Các phương thức hỗ trợ
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED;
            default:
                return false;
        }
    }

    private BigDecimal calculateSubtotal(List<ReqCreateOrderDTO.OrderDetailDTO> orderDetails) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ReqCreateOrderDTO.OrderDetailDTO detailDTO : orderDetails) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));
        }
        return subtotal;
    }

    private BigDecimal calculateSubtotalFromOrderDetails(List<OrderDetail> orderDetails) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderDetail detail : orderDetails) {
            subtotal = subtotal.add(detail.getProduct().getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        }
        return subtotal;
    }

    private BigDecimal calculateShippingFee(BigDecimal subtotal) {
        return subtotal.compareTo(BigDecimal.valueOf(200)) < 0
                ? BigDecimal.valueOf(10)
                : BigDecimal.ZERO;
    }

    private Coupon applyCoupon(String couponCode, BigDecimal subtotal) {
        // Logic áp dụng mã giảm giá (giả sử đã có repository cho Coupon)
        return null; // Placeholder
    }

    private BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal shippingFee, Coupon coupon) {
        BigDecimal total = subtotal.add(shippingFee);

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

        return total.max(BigDecimal.ZERO);
    }

    private List<OrderDetail> saveOrderDetails(Order order, List<ReqCreateOrderDTO.OrderDetailDTO> orderDetails) {
        return orderDetails.stream().map(detailDTO -> {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailDTO.getQuantity());
            return orderDetail;
        }).toList();
    }

    // Xóa đơn hàng hiện tại và tạo đơn hàng mới trống
    public void deleteOrder(User user) {
        // Tìm đơn hàng PENDING của người dùng
        Optional<Order> pendingOrder = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PENDING);
        if (pendingOrder.isPresent()) {
            // Xóa đơn hàng PENDING hiện tại
            orderRepository.delete(pendingOrder.get());
        }

        // Tạo đơn hàng mới trống
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setShippingFee(BigDecimal.ZERO); // Phí vận chuyển mặc định
        newOrder.setTotal(BigDecimal.ZERO); // Tổng tiền mặc định
        orderRepository.save(newOrder);
    }

    // Lấy lịch sử mua hàng của người dùng (đơn hàng không ở trạng thái PENDING)
    public Page<ResOrderResponseDTO> getOrderHistory(User user, Pageable pageable) {
        // Lấy tất cả đơn hàng không ở trạng thái PENDING
        return orderRepository.findByUserIdAndStatusNot(user.getId(), OrderStatus.PENDING.name(), pageable)
                .map(ResOrderResponseDTO::new); // Chuyển đổi từ Order sang ResOrderResponseDTO
    }

}