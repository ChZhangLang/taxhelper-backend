package com.zihao.taxhelperai.service.rule.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import com.zihao.taxhelperai.service.rule.DeductRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class HouseLoanRule implements DeductRule {

    private static final BigDecimal MONTHLY_AMOUNT = new BigDecimal("1000");

    private static final int MAX_MONTHS = 240;

    @Override
    public BigDecimal calculate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer isFirstHouse = (Integer) data.get("isFirstHouse");

        if (isFirstHouse == null || isFirstHouse != 1) {
            return BigDecimal.ZERO;
        }

        return MONTHLY_AMOUNT;
    }

    @Override
    public boolean validate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer isFirstHouse = (Integer) data.get("isFirstHouse");
        Integer totalMonths = (Integer) data.get("totalMonths");

        if (isFirstHouse == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择是否为首套住房");
        }

        if (isFirstHouse == 1 && totalMonths != null && totalMonths > MAX_MONTHS) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "住房贷款利息扣除最长不超过" + MAX_MONTHS + "个月");
        }

        return true;
    }

    @Override
    public Integer getType() {
        return 4;
    }
}