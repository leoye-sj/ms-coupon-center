package com.fortune.coupon.customer.service.impl;

import com.fortune.coupon.calculation.api.beans.ShoppingCart;
import com.fortune.coupon.calculation.api.beans.SimulationOrder;
import com.fortune.coupon.calculation.api.beans.SimulationResponse;
import com.fortune.coupon.customer.api.beans.RequestCoupon;
import com.fortune.coupon.customer.api.beans.SearchCoupon;
import com.fortune.coupon.customer.api.enums.CouponStatus;
import com.fortune.coupon.customer.converter.CouponConverter;
import com.fortune.coupon.customer.feign.CalculationService;
import com.fortune.coupon.customer.feign.TemplateService;
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

    /*@Autowired
    private WebClient.Builder webClientBuilder;*/

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CalculationService calculationService;


    @Override
    public Coupon requestCoupon(RequestCoupon requestCoupon) {
        // CouponTemplateInfo couponTemplateInfo = couponTemplateService.loadTemplateInfo(requestCoupon.getCouponTemplateId());
        /*CouponTemplateInfo couponTemplateInfo = webClientBuilder.build().get()
                .uri("http://coupon-template-ser/template/getTemplate?id="+requestCoupon.getCouponTemplateId())
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();*/
        CouponTemplateInfo couponTemplateInfo = templateService.getTemplate(requestCoupon.getCouponTemplateId());

        // ????????????????????????
        if (couponTemplateInfo == null) {
            log.error("invalid template id={}", requestCoupon.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }
        //??????????????????
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
            //???????????????
            Coupon example = Coupon.builder().userId(shoppingCart.getUserId())
                    .id(shoppingCart.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();
            coupon = couponDao.findAll(Example.of(example))
                    .stream().findFirst().orElseThrow(() -> new RuntimeException("Coupon not found"));
            CouponInfo couponInfo = CouponConverter.convertToCoupon(coupon);
            /*CouponTemplateInfo couponTemplateInfo = webClientBuilder.build().get()
                    .uri("http://coupon-template-ser/template/getTemplate?id="+coupon.getTemplateId())
                    .retrieve()
                    .bodyToMono(CouponTemplateInfo.class)
                    .block();*/
            CouponTemplateInfo couponTemplateInfo = templateService.getTemplate(coupon.getTemplateId());
            couponInfo.setCouponTemplateInfo(couponTemplateInfo);

            shoppingCart.setCouponInfos(Lists.newArrayList(couponInfo));

        }
        //????????????????????????
        // ShoppingCart checkOutInfo = couponCalculationService.calculateOrderPrice(shoppingCart);
        /*ShoppingCart checkOutInfo = webClientBuilder.build()
                .post()
                .uri("http://coupon-calculation-ser/calculator/checkout")
                .bodyValue(shoppingCart)
                .retrieve()
                .bodyToMono(ShoppingCart.class)
                .block();*/
       ShoppingCart checkOutInfo = calculationService.checkout(shoppingCart);
        if (coupon != null) {
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
        // ?????????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????template??????
        List<Coupon> couponList = couponDao.findByUserIdAndIdIn(simulationOrder.getUserId(), simulationOrder.getCouponIds());

        List<Long> couponTemplateIds = couponList.stream().map(Coupon::getTemplateId).collect(Collectors.toList());

        //Map<Long, CouponTemplateInfo> templateInfoMap = couponTemplateService.getTemplateInfoMap(couponTemplateIds);
        StringJoiner stringJoiner = new StringJoiner(",");
        couponTemplateIds.forEach(s->stringJoiner.add(s+""));
        log.info("couponTemplateIds:{}",stringJoiner.toString());
        /*Map<Long, CouponTemplateInfo> templateInfoMap = webClientBuilder.build()
                .get()
                .uri("http://coupon-template-ser/template/getBatch?ids="+stringJoiner.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long,CouponTemplateInfo>>() {})
                .block();*/
        Map<Long,CouponTemplateInfo> templateInfoMap = templateService.getTemplateInBatch(couponTemplateIds);

        couponList.stream().forEach(coupon -> coupon.setTemplateInfo(templateInfoMap.get(coupon.getTemplateId())));

        List<CouponInfo> couponInfoList = couponList.stream().map(CouponConverter::convertToCoupon).collect(Collectors.toList());
        simulationOrder.setCouponInfos(couponInfoList);

        /*SimulationResponse simulationResponse = webClientBuilder.build()
                .post()
                .uri("http://coupon-calculation-ser/calculator/simulate")
                .bodyValue(simulationOrder)
                .retrieve()
                .bodyToMono(SimulationResponse.class)
                .block();*/
        SimulationResponse simulationResponse = calculationService.simulate(simulationOrder);

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
        List<Long> couponTemplateIds = couponList.stream().map(coupon -> coupon.getTemplateId()).collect(Collectors.toList());
        // Map<Long, CouponTemplateInfo> templateInfoMap = couponTemplateService.getTemplateInfoMap(couponTemplateId);
        StringJoiner stringJoiner = new StringJoiner(",");
        couponTemplateIds.forEach(s->stringJoiner.add(s+""));
        log.info("couponTemplateIds:{}",stringJoiner.toString());
        /*Map<Long,CouponTemplateInfo> templateInfoMap = webClientBuilder.build()
                .get()
                .uri("http://coupon-template-ser/template/getBatch?ids="+stringJoiner.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long,CouponTemplateInfo>>() {})
                .block();*/
        Map<Long,CouponTemplateInfo> templateInfoMap = templateService.getTemplateInBatch(couponTemplateIds);
        couponList.forEach(coupon -> coupon.setTemplateInfo(templateInfoMap.get(coupon.getTemplateId())));
        return couponList.stream()
                .map(CouponConverter::convertToCoupon).collect(Collectors.toList());
    }
}
