package com.fortune.coupon.customer.feign;

import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "coupon-calculation-ser",path="/calculator")
public interface CalculationService {

    //订单结算
    @PostMapping("checkout")
    ShoppingCart checkout(ShoppingCart shoppingCart);

    //优惠券试算
    @PostMapping("/simulate")
    SimulationResponse simulate(SimulationOrder simulationOrder);
}
