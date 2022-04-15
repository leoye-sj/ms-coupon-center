package com.fortune.coupon.template.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.fortune.coupon.template.api.beans.CouponTemplateInfo;
import com.fortune.coupon.template.service.CouponTemplateService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

@Slf4j
public class CouponTemplateControllerBlockHandle {
    @Autowired
    private CouponTemplateService couponTemplateService;

    public static CouponTemplateInfo getTemplateBlock(Long id, BlockException e){
        log.info("sentinel getTemplate block handle", id);
        return null;
    }

    public static Map<Long, CouponTemplateInfo> getTemplateInBatchBlock(Collection<Long> ids,BlockException e) {
        log.info("sentinel getTemplateInBatch block handle", JSON.toJSONString(ids));
        return Maps.newHashMap();
    }
}
