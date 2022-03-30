package com.fortune.coupon.calculation.template.impl;

import com.fortune.coupon.calculation.template.AbstractCalculationRuleTemplate;
import com.fortune.coupon.calculation.template.CalculationRuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 在这个寂寞的夜晚，你需要金钱的陪伴
 *
 * 午夜10点到次日凌晨2点之间下单，优惠金额翻倍
 *
 */
@Slf4j
@Component
public class LonelyNightCalculationTemplate extends AbstractCalculationRuleTemplate implements CalculationRuleTemplate {


    @Override
    protected long calculateNewPrice(long orderTotalAmount, long shopTotalAmount, long quota) {

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay>=23||hourOfDay<2){
            quota*=2;
        }
        //优惠不能超过订单金额
        long saveAmount = shopTotalAmount<quota?shopTotalAmount:quota;
        return orderTotalAmount - saveAmount;
    }
}
