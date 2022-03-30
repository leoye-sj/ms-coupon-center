package com.fortune.coupon.customer.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCoupon {
    //用户领券
    @NotNull
    private long userId;

    //用户券模板
    @NotNull
    private long couponTemplateId;

}
