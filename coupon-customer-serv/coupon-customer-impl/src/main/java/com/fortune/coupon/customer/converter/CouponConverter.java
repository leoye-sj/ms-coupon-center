package com.fortune.coupon.customer.converter;

import com.fortune.coupon.dao.entity.Coupon;
import com.fortune.coupon.template.api.beans.CouponInfo;

public class CouponConverter {
    public static CouponInfo convertToCoupon(Coupon coupon) {

        return CouponInfo.builder()
                .id(coupon.getId())
                .status(coupon.getStatus().getCode())
                .templateId(coupon.getTemplateId())
                .shopId(coupon.getShopId())
                .userId(coupon.getUserId())
                .couponTemplateInfo(coupon.getTemplateInfo())
                .build();
    }
}
