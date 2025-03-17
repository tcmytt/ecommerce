package io.github.tcmytt.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.tcmytt.ecommerce.domain.Order;
import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateOrderDTO;
import io.github.tcmytt.ecommerce.domain.request.ReqUpdateOrderStatusDTO;
import io.github.tcmytt.ecommerce.service.OrderService;
import io.github.tcmytt.ecommerce.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtil securityUtil;

    public OrderController(OrderService orderService, SecurityUtil securityUtil) {
        this.orderService = orderService;
        this.securityUtil = securityUtil;
    }

    @Operation(summary = "Create order", description = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Order> createOrder(@RequestBody ReqCreateOrderDTO orderDTO) {
        // Lấy user hiện tại
        User currentUser = securityUtil.getCurrentUser();

        // Tạo đơn hàng
        Order createdOrder = orderService.createOrder(orderDTO, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Lấy danh sách đơn hàng của người dùng hiện tại
     */
    @Operation(summary = "Get all orders", description = "Returns a list of all orders with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<Order>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        // Lấy user hiện tại
        User currentUser = securityUtil.getCurrentUser();

        // Phân trang và sắp xếp
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));

        // Lấy danh sách đơn hàng
        Page<Order> orders = orderService.getOrdersByUser(currentUser.getId(), pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Xem chi tiết đơn hàng
     */
    @Operation(summary = "Get order by ID", description = "Returns an order by ID")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        // Lấy user hiện tại
        User currentUser = securityUtil.getCurrentUser();

        // Lấy đơn hàng
        Order order = orderService.getOrderByIdAndUser(orderId, currentUser.getId());

        return ResponseEntity.ok(order);
    }

    /**
     * Cập nhật trạng thái đơn hàng (chỉ admin)
     */
    @Operation(summary = "Update order status", description = "Update order status")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody ReqUpdateOrderStatusDTO statusDTO) {

        // Cập nhật trạng thái
        Order updatedOrder = orderService.updateOrderStatus(orderId, statusDTO.getStatus());

        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Hủy đơn hàng
     */
    @Operation(summary = "Cancel order", description = "Cancel an order")
    @ApiResponse(responseCode = "204", description = "Order canceled successfully")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        // Lấy user hiện tại
        User currentUser = securityUtil.getCurrentUser();

        // Hủy đơn hàng
        orderService.cancelOrder(orderId, currentUser.getId());

        return ResponseEntity.noContent().build();
    }
}
