package com.fortune.coupon.calculation.api.beans;

import com.fortune.coupon.template.api.beans.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    //商品列表
    @NotEmpty
    private List<Product> products;

    //使用的优惠券信息
    private List<CouponInfo> couponInfos;

    private Long couponId;

    //订单的最终价格
    private long cost;
    //用户ID
    @NotNull
    private long userId;

}
