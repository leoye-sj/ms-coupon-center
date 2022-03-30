package com.fortune.coupon.template.api.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    //优惠的数值
    //满减券  减掉的金额
    //打折券  折扣数  90 表示9折 95 表示9.5折
    //随机立减券  //最高随机立减金额
    //晚间特别优惠券  //优惠额
    private Long quota;

    //最低达到多少钱才能使用优惠券
    private Long threshold;
}
