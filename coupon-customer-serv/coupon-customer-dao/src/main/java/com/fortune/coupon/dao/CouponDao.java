package com.fortune.coupon.dao;

import com.fortune.coupon.dao.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponDao extends JpaRepository<Coupon,Long> {
    long countByUserIdAndTemplateId(Long userId, Long templateId);
    List<Coupon> findByUserIdAndIdIn(long userId,List<Long> couponIds);
}
