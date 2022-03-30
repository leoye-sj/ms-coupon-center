package com.fortune.coupon.calculation.service;


import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface CouponCalculationService {
    /**
     * 计算优惠价格
     * @param cart
     * @return
     */
    ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart cart);

    /**订单价格试算
     * 每张优惠券的优惠价格
     * @param cart
     * @return
     */
    SimulationResponse simulateOrder(@RequestBody SimulationOrder cart);
}
