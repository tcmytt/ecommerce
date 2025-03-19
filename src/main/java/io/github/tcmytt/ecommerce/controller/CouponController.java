package io.github.tcmytt.ecommerce.controller;

import io.github.tcmytt.ecommerce.domain.request.ReqCreateCouponDTO;
import io.github.tcmytt.ecommerce.domain.response.ResCouponResponseDTO;
import io.github.tcmytt.ecommerce.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Create a coupon", description = "Create a new coupon")
    @ApiResponse(responseCode = "201", description = "Coupon created successfully")
    @PostMapping
    public ResponseEntity<ResCouponResponseDTO> createCoupon(@RequestBody ReqCreateCouponDTO dto) {
        ResCouponResponseDTO response = couponService.createCoupon(dto);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Get coupons by status", description = "Get all coupons by status")
    @ApiResponse(responseCode = "200", description = "Coupons retrieved successfully")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ResCouponResponseDTO>> getCouponsByStatus(@PathVariable Boolean status) {
        List<ResCouponResponseDTO> coupons = couponService.getCouponsByStatus(status);
        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "Get coupon by code", description = "Get a coupon by its code")
    @ApiResponse(responseCode = "200", description = "Coupon retrieved successfully")
    @GetMapping("/code/{code}")
    public ResponseEntity<ResCouponResponseDTO> getCouponByCode(@PathVariable String code) {
        ResCouponResponseDTO coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }

    @Operation(summary = "Update a coupon", description = "Update an existing coupon")
    @ApiResponse(responseCode = "200", description = "Coupon updated successfully")
    @PutMapping("/{couponId}")
    public ResponseEntity<ResCouponResponseDTO> updateCoupon(
            @PathVariable Long couponId,
            @RequestBody ReqCreateCouponDTO dto) {
        ResCouponResponseDTO response = couponService.updateCoupon(couponId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a coupon", description = "Delete an existing coupon")
    @ApiResponse(responseCode = "204", description = "Coupon deleted successfully")
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}