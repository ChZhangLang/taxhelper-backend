package com.zihao.taxhelperai.service.rule.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import com.zihao.taxhelperai.service.rule.DeductRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ContinueEducationRule implements DeductRule {

    private static final BigDecimal DEGREE_AMOUNT = new BigDecimal("400");
    private static final BigDecimal CERTIFICATE_AMOUNT = new BigDecimal("300");

    @Override
    public BigDecimal calculate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer educationType = (Integer) data.get("educationType");

        if (educationType == null) {
            return BigDecimal.ZERO;
        }

        if (educationType == 1) {
            return DEGREE_AMOUNT;
        } else if (educationType == 2) {
            return CERTIFICATE_AMOUNT;
        }

        return BigDecimal.ZERO;
    }

    @Override
    public boolean validate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer educationType = (Integer) data.get("educationType");

        if (educationType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择教育类型");
        }

        if (educationType != 1 && educationType != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "教育类型必须为1-学历教育或2-职业资格教育");
        }

        return true;
    }

    @Override
    public Integer getType() {
        return 2;
    }
}