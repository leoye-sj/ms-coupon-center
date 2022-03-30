package com.fortune.coupon.template.dao.convert;

import com.alibaba.fastjson.JSON;
import com.fortune.coupon.template.api.rules.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class RuleConverter implements AttributeConverter<TemplateRule,String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule attribute) {
        return JSON.toJSONString(attribute);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String dbData) {
        return JSON.parseObject(dbData, TemplateRule.class);
    }
}
