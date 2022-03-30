package com.fortune.coupon.template.api.beans;

import com.fortune.coupon.template.api.rules.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateInfo {


    private long id;
    @NotNull
    private String name;  //优惠券名称
    @NotNull
    private String desc;  //优惠券描述
    @NotNull
    private String type;  //优惠券类型

    private Long shopId;  //优惠券适用门店
    @NotNull
    private TemplateRule rule;  //优惠券适用规则

    private Boolean available;  //是否可用状态
}
