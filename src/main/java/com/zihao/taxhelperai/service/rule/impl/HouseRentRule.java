package com.zihao.taxhelperai.service.rule.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import com.zihao.taxhelperai.service.rule.DeductRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class HouseRentRule implements DeductRule {

    private static final BigDecimal TIER_1_AMOUNT = new BigDecimal("1500");
    private static final BigDecimal TIER_2_AMOUNT = new BigDecimal("1100");
    private static final BigDecimal TIER_3_AMOUNT = new BigDecimal("800");

    @Override
    public BigDecimal calculate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer cityLevel = (Integer) data.get("cityLevel");
        Integer hasHouseInCity = (Integer) data.get("hasHouseInCity");

        if (cityLevel == null || hasHouseInCity == 1) {
            return BigDecimal.ZERO;
        }

        switch (cityLevel) {
            case 1:
                return TIER_1_AMOUNT;
            case 2:
                return TIER_2_AMOUNT;
            case 3:
                return TIER_3_AMOUNT;
            default:
                return TIER_3_AMOUNT;
        }
    }

    @Override
    public boolean validate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer cityLevel = (Integer) data.get("cityLevel");

        if (cityLevel == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择城市等级");
        }

        if (cityLevel < 1 || cityLevel > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "城市等级必须为1-3");
        }

        return true;
    }

    @Override
    public Integer getType() {
        return 5;
    }
}