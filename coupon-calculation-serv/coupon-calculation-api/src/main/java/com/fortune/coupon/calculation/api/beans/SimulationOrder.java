package com.fortune.coupon.calculation.api.beans;

import com.fortune.coupon.template.api.beans.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationOrder {

    //商品列表
    private List<Product> products;
    //优惠券列表
    private List<Long> couponIds;
   //优惠券信息
    private List<CouponInfo> CouponInfos;
    //userId
    private long userId;


}
