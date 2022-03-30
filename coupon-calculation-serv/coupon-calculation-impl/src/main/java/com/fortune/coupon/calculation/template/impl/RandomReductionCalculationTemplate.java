package com.fortune.coupon.calculation.template.impl;

import com.fortune.coupon.calculation.template.AbstractCalculationRuleTemplate;
import com.fortune.coupon.calculation.template.CalculationRuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;


// 随机减钱
@Slf4j
@Component
public class RandomReductionCalculationTemplate extends AbstractCalculationRuleTemplate implements CalculationRuleTemplate {

    @Override
    protected long calculateNewPrice(long totalAmount, long shopAmount, long quota) {
        // 计算使用优惠券之后的价格
        Long maxSave = Math.min(shopAmount,quota);
        int reductionAmount = new Random().nextInt(maxSave.intValue());
        long newPrice = totalAmount-reductionAmount;

        log.debug("original price={}, new price={}", totalAmount, newPrice );
        return newPrice;
    }
}
