package com.fortune.coupon.calculation.template.impl;

import com.fortune.coupon.calculation.template.AbstractCalculationRuleTemplate;
import com.fortune.coupon.calculation.template.CalculationRuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 满减优惠券计算规则
 */
@Slf4j
@Component
public class MoneyOffCalculationTemplate extends AbstractCalculationRuleTemplate implements CalculationRuleTemplate {

    @Override
    protected long calculateNewPrice(long totalAmount, long shopAmount, long quota) {
        // saveAmount是扣减的价格
        // 如果当前门店的商品总价<quota，那么最多只能扣减shopAmount的钱数
        Long saveAmount = shopAmount < quota ? shopAmount : quota;
        return totalAmount - saveAmount;
    }
}
