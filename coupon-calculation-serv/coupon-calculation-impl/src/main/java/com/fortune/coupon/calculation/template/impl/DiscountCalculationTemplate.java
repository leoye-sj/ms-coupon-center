package com.fortune.coupon.calculation.template.impl;

import com.fortune.coupon.calculation.template.AbstractCalculationRuleTemplate;
import com.fortune.coupon.calculation.template.CalculationRuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 打折优惠券
 */
@Slf4j
@Component
public class DiscountCalculationTemplate extends AbstractCalculationRuleTemplate implements CalculationRuleTemplate {

    @Override
    protected long calculateNewPrice(long totalAmount, long shopAmount, long quota) {
        // 计算使用优惠券之后的价格
        long newShopAmount = BigDecimal.valueOf(shopAmount)
                .multiply(BigDecimal.valueOf(quota).divide(new BigDecimal(100)))
                .setScale(0,BigDecimal.ROUND_HALF_UP).longValue();
        long saveAmount = shopAmount - newShopAmount;
        long newPrice = totalAmount-saveAmount;
        log.debug("original price={}, new price={}", totalAmount, newPrice);
        return newPrice;
    }
}
