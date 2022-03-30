package com.fortune.coupon.dao.converter;

import com.fortune.coupon.customer.api.enums.CouponStatus;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

@Component
public class CouponStatusConverter implements AttributeConverter<CouponStatus,Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer dbData) {
        return CouponStatus.convert(dbData);
    }
}
