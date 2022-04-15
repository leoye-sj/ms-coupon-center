package com.fortune.coupon.customer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import com.fortune.coupon.customer.api.beans.RequestCoupon;
import com.fortune.coupon.customer.api.beans.SearchCoupon;
import com.fortune.coupon.customer.service.CouponCustomerService;
import com.fortune.coupon.dao.entity.Coupon;
import com.fortune.coupon.template.api.beans.CouponInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("coupon-customer")
@RefreshScope
public class CouponCustomerController {

    @Value("${disableCouponRequest:false}")
    private boolean disableRequestCoupon;

    @Autowired
    private CouponCustomerService customerService;

    @PostMapping("requestCoupon")
    @SentinelResource(value = "requestCoupon")
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) {
        if (disableRequestCoupon) {
            log.info("暂停领券");
            return null;
        }
        return customerService.requestCoupon(request);
    }

    // 用户删除优惠券
    @DeleteMapping("deleteCoupon")
    public void deleteCoupon(@RequestParam("userId") Long userId,
                                       @RequestParam("couponId") Long couponId) {
        customerService.deleteCoupon(userId, couponId);
    }

    // 用户模拟计算每个优惠券的优惠价格
    @PostMapping("simulateOrder")
    public SimulationResponse simulate(@Valid @RequestBody SimulationOrder order) {
        return customerService.simulationOrderPrice(order);
    }

    // ResponseEntity - 指定返回状态码 - 可以作为一个课后思考题
    @PostMapping("placeOrder")
    public ShoppingCart checkout(@Valid @RequestBody ShoppingCart info) {
        return customerService.placeOrder(info);
    }


    // 实现的时候最好封装一个search object类
    @PostMapping("findCoupon")
    @SentinelResource(value = "findCoupon",blockHandlerClass = CouponCustomerControllerBlockHandle.class,blockHandler = "findCouponBlock")
    public List<CouponInfo> findCoupon(@Valid @RequestBody SearchCoupon request) {
        return customerService.findCoupon(request);
    }

}
