package com.fortune.coupon.customer.service.impl;

import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import com.fortune.coupon.customer.api.beans.RequestCoupon;
import com.fortune.coupon.customer.api.beans.SearchCoupon;
import com.fortune.coupon.customer.api.enums.CouponStatus;
import com.fortune.coupon.customer.converter.CouponConverter;
import com.fortune.coupon.customer.service.CouponCustomerService;
import com.fortune.coupon.dao.CouponDao;
import com.fortune.coupon.dao.entity.Coupon;
import com.fortune.coupon.template.api.beans.CouponInfo;
import com.fortune.coupon.template.api.beans.CouponTemplateInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Override
    public Coupon requestCoupon(RequestCoupon requestCoupon) {
        // CouponTemplateInfo couponTemplateInfo = couponTemplateService.loadTemplateInfo(requestCoupon.getCouponTemplateId());
        CouponTemplateInfo couponTemplateInfo = webClientBuilder.build().get()
                .uri("http://coupon-template-ser/template/getTemplate?id="+requestCoupon.getCouponTemplateId())
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();

        // 模板不存在则报错
        if (couponTemplateInfo == null) {
            log.error("invalid template id={}", requestCoupon.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }
        //过期或不可用
        Calendar calendar = Calendar.getInstance();
        if(couponTemplateInfo.getRule().getDeadline()!=null && calendar.getTimeInMillis()>couponTemplateInfo.getRule().getDeadline()
          || BooleanUtils.isFalse(couponTemplateInfo.getAvailable())){
            log.error("template is not available id={}", requestCoupon.getCouponTemplateId());
            throw new IllegalArgumentException("template is unavailable");
        }

        long ownedTotal = couponDao.countByUserIdAndTemplateId(requestCoupon.getUserId(), couponTemplateInfo.getId());
        if(ownedTotal>=couponTemplateInfo.getRule().getLimitation()){
            log.error("exceeds maximum number");
            throw new IllegalArgumentException("exceeds maximum number");
        }

        Coupon coupon = Coupon.builder()
                .userId(requestCoupon.getUserId())
                .templateId(requestCoupon.getCouponTemplateId())
                .status(CouponStatus.AVAILABLE)
                .shopId(couponTemplateInfo.getShopId())
                .build();
        couponDao.save(coupon);


        return couponDao.save(coupon);
    }

    @Override
    @Transactional
    public ShoppingCart placeOrder(ShoppingCart shoppingCart) {
        if (CollectionUtils.isEmpty(shoppingCart.getProducts())) {
            log.error("invalid check out request, order={}", shoppingCart);
            throw new IllegalArgumentException("cart if empty");
        }
        Coupon coupon = null;
        if (shoppingCart.getCouponId() != null) {
            //查出优惠券
            Coupon example = Coupon.builder().userId(shoppingCart.getUserId())
                    .id(shoppingCart.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();
            coupon = couponDao.findAll(Example.of(example))
                    .stream().findFirst().orElseThrow(() -> new RuntimeException("Coupon not found"));
            CouponInfo couponInfo = CouponConverter.convertToCoupon(coupon);
            CouponTemplateInfo couponTemplateInfo = webClientBuilder.build().get()
                    .uri("http://coupon-template-ser/template/getTemplate?id="+coupon.getTemplateId())
                    .retrieve()
                    .bodyToMono(CouponTemplateInfo.class)
                    .block();
            couponInfo.setCouponTemplateInfo(couponTemplateInfo);

            shoppingCart.setCouponInfos(Lists.newArrayList(couponInfo));

        }
        //计算订单优惠价格
        // ShoppingCart checkOutInfo = couponCalculationService.calculateOrderPrice(shoppingCart);
        ShoppingCart checkOutInfo = webClientBuilder.build()
                .post()
                .uri("http://coupon-calculation-ser/calculator/checkout")
                .bodyValue(shoppingCart)
                .retrieve()
                .bodyToMono(ShoppingCart.class)
                .block();
        if (coupon != null) {
            //如果优惠券没有被结算掉，而用户传递了优惠券，报错提示该订单满足不了优惠条件
            if (CollectionUtils.isEmpty(shoppingCart.getCouponInfos())) {
                log.error("cannot apply coupon to order, couponId={}", coupon.getId());
                throw new IllegalArgumentException("coupon is not applicable to this order");
            }
            log.info("update coupon status to used, couponId={}", coupon.getId());
            coupon.setStatus(CouponStatus.USED);
        }
        return checkOutInfo;
    }

    @Override
    public SimulationResponse simulationOrderPrice(SimulationOrder simulationOrder) {
        // 挨个循环，把优惠券信息加载出来
        // 高并发场景下不能这么一个个循环，更好的做法是批量查询
        // 而且券模板一旦创建不会改内容，所以在创建端做数据异构放到缓存里，使用端从缓存捞template信息
        List<Coupon> couponList = couponDao.findByUserIdAndIdIn(simulationOrder.getUserId(), simulationOrder.getCouponIds());

        List<Long> couponTemplateIds = couponList.stream().map(Coupon::getTemplateId).collect(Collectors.toList());

        //Map<Long, CouponTemplateInfo> templateInfoMap = couponTemplateService.getTemplateInfoMap(couponTemplateIds);
        StringJoiner stringJoiner = new StringJoiner(",");
        couponTemplateIds.forEach(s->stringJoiner.add(s+""));
        log.info("couponTemplateIds:{}",stringJoiner.toString());
        Map<Long, CouponTemplateInfo> templateInfoMap = webClientBuilder.build()
                .get()
                .uri("http://coupon-template-ser/template/getBatch?ids="+stringJoiner.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long,CouponTemplateInfo>>() {})
                .block();

        couponList.stream().forEach(coupon -> coupon.setTemplateInfo(templateInfoMap.get(coupon.getTemplateId())));

        List<CouponInfo> couponInfoList = couponList.stream().map(CouponConverter::convertToCoupon).collect(Collectors.toList());
        simulationOrder.setCouponInfos(couponInfoList);

        SimulationResponse simulationResponse = webClientBuilder.build()
                .post()
                .uri("http://coupon-calculation-ser/calculator/simulate")
                .bodyValue(simulationOrder)
                .retrieve()
                .bodyToMono(SimulationResponse.class)
                .block();

        return simulationResponse;
    }

    @Override
    public void deleteCoupon(long userId, long couponId) {
        Coupon example = Coupon.builder().userId(userId).id(couponId).status(CouponStatus.AVAILABLE).build();
        Coupon coupon = couponDao.findAll(Example.of(example))
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setStatus(CouponStatus.INACTIVE);
        couponDao.save(coupon);

    }

    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {
        Coupon example = Coupon.builder()
                .userId(request.getUserId())
                .shopId(request.getShopId())
                .status(CouponStatus.convert(request.getCouponStatus())).build();
        List<Coupon> couponList = couponDao.findAll(Example.of(example));
        if (CollectionUtils.isEmpty(couponList)) {
            return Lists.newArrayList();
        }
        List<Long> couponTemplateId = couponList.stream().map(coupon -> coupon.getTemplateId()).collect(Collectors.toList());
        // Map<Long, CouponTemplateInfo> templateInfoMap = couponTemplateService.getTemplateInfoMap(couponTemplateId);
        StringJoiner stringJoiner = new StringJoiner(",");
        couponTemplateId.forEach(s->stringJoiner.add(s+""));
        log.info("couponTemplateIds:{}",stringJoiner.toString());
        Map<Long,CouponTemplateInfo> templateInfoMap = webClientBuilder.build()
                .get()
                .uri("http://coupon-template-ser/template/getBatch?ids="+stringJoiner.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long,CouponTemplateInfo>>() {})
                .block();
        couponList.forEach(coupon -> coupon.setTemplateInfo(templateInfoMap.get(coupon.getTemplateId())));

        return couponList.stream()
                .map(CouponConverter::convertToCoupon).collect(Collectors.toList());
    }
}
