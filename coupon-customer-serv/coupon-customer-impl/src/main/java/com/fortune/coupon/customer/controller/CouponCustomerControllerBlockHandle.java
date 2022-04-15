package com.fortune.coupon.customer.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fortune.coupon.customer.api.beans.RequestCoupon;
import com.fortune.coupon.customer.api.beans.SearchCoupon;
import com.fortune.coupon.customer.service.CouponCustomerService;
import com.fortune.coupon.dao.entity.Coupon;
import com.fortune.coupon.template.api.beans.CouponInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Slf4j
public class CouponCustomerControllerBlockHandle {

    public static List<CouponInfo> findCouponBlock(SearchCoupon request, BlockException e) {
        log.info("sentinel findCoupon block handle");
        return Lists.newArrayList();
    }

    public static Coupon requestCoupon(RequestCoupon request,BlockException e) {
        log.info("sentinel requestCoupon block handle");
        return null;
    }
}
