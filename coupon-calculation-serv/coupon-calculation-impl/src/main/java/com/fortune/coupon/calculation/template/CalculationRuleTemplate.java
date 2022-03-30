package com.fortune.coupon.calculation.template;

import com.fortune.coupon.calculation.api.beans.ShoppingCart;

public interface CalculationRuleTemplate {
   ShoppingCart calculate(ShoppingCart shoppingCart);
}
