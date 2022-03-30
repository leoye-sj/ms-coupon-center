package com.fortune.coupon.calculation.api.beans;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

@Data
public class SimulationResponse {

    //最省钱的优惠券
    private long bestCouponId;
    //每个coupon对应的order价格
    private Map<Long,Long> couponToorderPrice = Maps.newHashMap();
}
