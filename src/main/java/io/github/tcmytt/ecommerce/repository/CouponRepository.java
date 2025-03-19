package io.github.tcmytt.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    List<Coupon> findByStatus(Boolean status); // Tìm tất cả coupon theo trạng thái
    // Coupon findByCode(String code); // Tìm coupon theo mã
}
