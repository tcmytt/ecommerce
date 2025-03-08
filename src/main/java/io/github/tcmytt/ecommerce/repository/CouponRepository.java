package io.github.tcmytt.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tcmytt.ecommerce.domain.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
