package com.fortune.coupon.template.sentinelparser;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class LimitFlowOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        log.info("request={},header={}",httpServletRequest.getParameterMap(),httpServletRequest.getHeaderNames());
        return httpServletRequest.getHeader("limitFlowSource");
    }
}
