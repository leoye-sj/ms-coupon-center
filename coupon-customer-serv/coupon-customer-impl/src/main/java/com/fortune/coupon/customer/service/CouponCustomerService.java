package com.fortune.coupon.customer.service;

import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import com.fortune.coupon.customer.api.beans.RequestCoupon;
import com.fortune.coupon.customer.api.beans.SearchCoupon;
import com.fortune.coupon.dao.entity.Coupon;
import com.fortune.coupon.template.api.beans.CouponInfo;

import java.util.List;

public interface CouponCustomerService {
    //领券
    Coupon requestCoupon(RequestCoupon requestCoupon);
    //核销优惠券
    ShoppingCart placeOrder(ShoppingCart shoppingCart);
    //优惠券金额试算
    SimulationResponse simulationOrderPrice(SimulationOrder simulationOrder);
    //用户删除优惠券
    void deleteCoupon(long userId,long couponId);
    //用户查询优惠券
    List<CouponInfo> findCoupon(SearchCoupon request);
}
