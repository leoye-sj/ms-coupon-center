package com.fortune.coupon.calculation.template;

import com.fortune.coupon.calculation.api.beans.Product;
import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.template.api.beans.CouponTemplateInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCalculationRuleTemplate implements CalculationRuleTemplate {


    @Override
    public ShoppingCart calculate(ShoppingCart shoppingCart) {
        long orderTotalAmount = getTotalPrice(shoppingCart.getProducts());
        Map<Long,Long> sumAmount = getTotalPriceGroupByShop(shoppingCart.getProducts());
        CouponTemplateInfo template = shoppingCart.getCouponInfos().get(0).getCouponTemplateInfo();
        //最低消费限制
        long threshHold = template.getRule().getDiscount().getThreshold();
        //优惠金额或者打折比例
        long  quota = template.getRule().getDiscount().getQuota();
        //如果优惠券未指定shopId，则shopTotalAmount = orderTotalAmount
        //如果指定了shopId,则shopTotalAmount = 对应门店的总价
        Long shopId = template.getShopId();
        Long shopTotalAmount = shopId==null?orderTotalAmount:sumAmount.get(shopId);
        //商品金额未到折扣阈值
        if(shopTotalAmount==null || shopTotalAmount<threshHold){
            log.warn("Totals of amount not meet, ur coupons are not applicable to this order");
            shoppingCart.setCost(orderTotalAmount);
            shoppingCart.setCouponInfos(Collections.emptyList());
            return shoppingCart;
        }
        //可以使用优惠券
        long newCost = calculateNewPrice(orderTotalAmount,shopTotalAmount,quota);
        if(newCost<minCost()){
            newCost=minCost();
        }
        shoppingCart.setCost(newCost);
        log.debug("original price={}, new price={}", orderTotalAmount, newCost);
        return shoppingCart;
    }

    private long minCost() {
        return 1l;
    }

    protected Map<Long, Long> getTotalPriceGroupByShop(List<Product> products) {

        return products.stream().collect(
                Collectors.groupingBy(product->product.getShopId(),Collectors.summingLong(p->p.getPrice()*p.getCount())));
    }

    protected  long getTotalPrice(List<Product> products){
        return products.stream().mapToLong(product->product.getPrice()*product.getCount())
                .sum();
    }

    protected abstract  long calculateNewPrice(long orderTotalAmount,long shopTotalAmount,long quota);
}
