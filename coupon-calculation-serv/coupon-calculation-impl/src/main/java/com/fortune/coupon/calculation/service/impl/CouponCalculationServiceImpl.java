package com.fortune.coupon.calculation.service.impl;

import com.alibaba.fastjson.JSON;
import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import com.fortune.coupon.calculation.service.CouponCalculationService;
import com.fortune.coupon.calculation.template.CalculationRuleTemplate;
import com.fortune.coupon.calculation.template.CouponCalculationTemplateFactory;
import com.fortune.coupon.template.api.beans.CouponInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@Slf4j
public class CouponCalculationServiceImpl implements CouponCalculationService {
    @Autowired
    private CouponCalculationTemplateFactory couponCalculationTemplateFactory;
    @Override
    public ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart cart) {
        log.info("calculate order price: {}", JSON.toJSONString(cart));

        CalculationRuleTemplate template = couponCalculationTemplateFactory.getTemplate(cart);
        return template.calculate(cart);
    }

    @Override
    public SimulationResponse simulateOrder(@RequestBody SimulationOrder simulationOrder) {
        List<CouponInfo> couponInfoList = simulationOrder.getCouponInfos();
        SimulationResponse simulationResponse = new SimulationResponse();
        long lowestPrice = Long.MAX_VALUE;

        for(CouponInfo couponInfo:couponInfoList){
            ShoppingCart simulateShoppingCart = new ShoppingCart();
            simulateShoppingCart.setProducts(simulationOrder.getProducts());
            simulateShoppingCart.setCouponInfos(Lists.newArrayList(couponInfo));
             simulateShoppingCart = this.calculateOrderPrice(simulateShoppingCart);
             simulationResponse.getCouponToorderPrice().put(couponInfo.getId(),simulateShoppingCart.getCost());
             if(simulateShoppingCart.getCost()<lowestPrice){
                 simulationResponse.setBestCouponId(couponInfo.getId());
                 lowestPrice=simulateShoppingCart.getCost();
             }
        }
        return simulationResponse;
    }
}
