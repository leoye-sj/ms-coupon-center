package com.fortune.coupon.template.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponInfo {

    private Long id;

    //优惠券模板id 冗余字段
    private Long templateId;
    //领券用户id
    private Long userId;
    //适用门店
    private Long shopId;

    private Integer status;

    private CouponTemplateInfo couponTemplateInfo;
}
