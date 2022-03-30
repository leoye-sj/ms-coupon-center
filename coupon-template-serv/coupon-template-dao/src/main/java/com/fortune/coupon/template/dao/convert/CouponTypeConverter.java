package com.fortune.coupon.template.dao.convert;

import com.fortune.coupon.template.api.enums.CouponType;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class CouponTypeConverter implements AttributeConverter<CouponType,String> {
    @Override
    public String convertToDatabaseColumn(CouponType attribute) {
        return attribute.getCode();
    }

    @Override
    public CouponType convertToEntityAttribute(String dbData) {
        return CouponType.convert(dbData);
    }
}
