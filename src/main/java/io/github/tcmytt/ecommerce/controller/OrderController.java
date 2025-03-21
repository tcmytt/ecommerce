package io.github.tcmytt.ecommerce.controller;

import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.enums.OrderStatus;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateOrderDTO;
import io.github.tcmytt.ecommerce.domain.response.ResOrderResponseDTO;
import io.github.tcmytt.ecommerce.service.OrderService;
import io.github.tcmytt.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @Operation(summary = "Create an order", description = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @PostMapping
    public ResponseEntity<ResOrderResponseDTO> createOrder(@RequestBody ReqCreateOrderDTO dto) {
        // Lấy thông tin người dùng từ Security Context
        User user = getCurrentUser(); // Giả sử có method lấy user từ context
        return ResponseEntity.status(201).body(orderService.createOrder(dto, user));
    }

    @Operation(summary = "Update an order", description = "Update an existing order")
    @ApiResponse(responseCode = "200", description = "Order updated successfully")
    @PutMapping("/{orderId}")
    public ResponseEntity<ResOrderResponseDTO> updateOrder(
            @PathVariable Long orderId,
            @RequestBody ReqCreateOrderDTO dto) {
        User user = getCurrentUser(); // Lấy thông tin người dùng
        return ResponseEntity.ok(orderService.updateOrder(orderId, dto, user));
    }

    @Operation(summary = "Update order status", description = "Update the status of an existing order")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ResOrderResponseDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @Operation(summary = "Get current order", description = "Get the current pending order of the user")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No pending order found")
    @GetMapping("/current")
    public ResponseEntity<ResOrderResponseDTO> getCurrentOrder() {
        User user = getCurrentUser(); // Lấy thông tin người dùng
        return ResponseEntity.ok(orderService.getCurrentOrder(user));
    }

    // Method giả định để lấy thông tin người dùng từ Security Context
    private User getCurrentUser() {
        // Logic lấy user từ context (ví dụ: SecurityUtil.getCurrentUser())
        // Lấy user từ SecurityContext
        var userOptional = SecurityContextHolder.getContext().getAuthentication();
        if (userOptional == null || userOptional.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        User user = userService.handleGetUserByUsername(userOptional.getName());
        return user; // Placeholder
    }

    @Operation(summary = "Delete current order", description = "Delete the current pending order and create a new empty order")
    @ApiResponse(responseCode = "204", description = "Order deleted successfully")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteOrder() {
        User user = getCurrentUser(); // Lấy thông tin người dùng
        orderService.deleteOrder(user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get order history", description = "Get all orders of the user that are not PENDING")
    @ApiResponse(responseCode = "200", description = "Order history retrieved successfully")
    @GetMapping("/history")
    public ResponseEntity<Page<ResOrderResponseDTO>> getOrderHistory(Pageable pageable) {
        // Lấy thông tin người dùng từ Security Context
        User user = getCurrentUser(); // Giả sử có method lấy user từ context
        return ResponseEntity.ok(orderService.getOrderHistory(user, pageable));
    }
}