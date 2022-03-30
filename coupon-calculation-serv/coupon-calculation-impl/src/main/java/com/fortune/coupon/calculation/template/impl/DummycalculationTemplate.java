package com.fortune.coupon.calculation.template.impl;

import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.template.AbstractCalculationRuleTemplate;
import com.fortune.coupon.calculation.template.CalculationRuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 空实现
 */
@Slf4j
@Component
public class DummycalculationTemplate extends AbstractCalculationRuleTemplate implements CalculationRuleTemplate {

    @Override
    public ShoppingCart calculate(ShoppingCart order) {
        // 获取订单总价
        Long orderTotalAmount = getTotalPrice(order.getProducts());

        order.setCost(orderTotalAmount);
        return order;
    }

    @Override
    protected long calculateNewPrice(long orderTotalAmount, long shopTotalAmount, long quota) {
        return 0;
    }
}
