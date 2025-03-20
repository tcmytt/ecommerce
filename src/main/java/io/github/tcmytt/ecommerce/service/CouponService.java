package io.github.tcmytt.ecommerce.service;

import io.github.tcmytt.ecommerce.domain.Coupon;
import io.github.tcmytt.ecommerce.domain.request.ReqCreateCouponDTO;
import io.github.tcmytt.ecommerce.domain.response.ResCouponResponseDTO;
import io.github.tcmytt.ecommerce.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // Tạo coupon mới
    public ResCouponResponseDTO createCoupon(ReqCreateCouponDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setType(dto.getType());
        coupon.setCode(dto.getCode());
        coupon.setValue(dto.getValue());
        coupon.setStartDate(dto.getStartDate());
        coupon.setEndDate(dto.getEndDate());
        coupon.setMinSpend(dto.getMinSpend());
        coupon.setMaxSpend(dto.getMaxSpend());
        coupon.setUsesPerUser(dto.getUsesPerUser());
        coupon.setUsesPerCoupon(dto.getUsesPerCoupon());
        coupon.setStatus(dto.getStatus());

        Coupon savedCoupon = couponRepository.save(coupon);
        return new ResCouponResponseDTO(savedCoupon);
    }

    // getAllCoupons
    public List<ResCouponResponseDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
                .map(ResCouponResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Lấy coupon theo id
    public ResCouponResponseDTO getCouponById(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));
        return new ResCouponResponseDTO(coupon);
    }

    // Lấy danh sách coupon theo trạng thái
    public List<ResCouponResponseDTO> getCouponsByStatus(Boolean status) {
        List<Coupon> coupons = couponRepository.findByStatus(status);
        return coupons.stream()
                .map(ResCouponResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Lấy coupon theo mã
    public ResCouponResponseDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code).orElse(null);
        if (coupon == null) {
            throw new RuntimeException("Coupon không tồn tại");
        }
        return new ResCouponResponseDTO(coupon);
    }

    // Cập nhật coupon
    public ResCouponResponseDTO updateCoupon(Long couponId, ReqCreateCouponDTO dto) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));

        coupon.setType(dto.getType());
        coupon.setCode(dto.getCode());
        coupon.setValue(dto.getValue());
        coupon.setStartDate(dto.getStartDate());
        coupon.setEndDate(dto.getEndDate());
        coupon.setMinSpend(dto.getMinSpend());
        coupon.setMaxSpend(dto.getMaxSpend());
        coupon.setUsesPerUser(dto.getUsesPerUser());
        coupon.setUsesPerCoupon(dto.getUsesPerCoupon());
        coupon.setStatus(dto.getStatus());

        Coupon updatedCoupon = couponRepository.save(coupon);
        return new ResCouponResponseDTO(updatedCoupon);
    }

    // Xóa coupon
    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }
}