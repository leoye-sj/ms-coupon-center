package com.fortune.coupon.template.api.rules;

import com.fortune.coupon.template.api.rules.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

    //可以享受的折扣
    private Discount discount;

    //每个人最多可以领券的数量
    private Integer limitation;

    //过期时间
    private Long deadline;
}
