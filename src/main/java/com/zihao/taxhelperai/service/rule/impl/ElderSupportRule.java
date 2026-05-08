package com.zihao.taxhelperai.service.rule.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import com.zihao.taxhelperai.service.rule.DeductRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class ElderSupportRule implements DeductRule {

    private static final BigDecimal ONLY_CHILD_AMOUNT = new BigDecimal("3000");
    private static final BigDecimal NON_ONLY_CHILD_MAX = new BigDecimal("1500");

    private static final int MIN_ELDER_AGE = 60;

    @Override
    public BigDecimal calculate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer isOnlyChild = (Integer) data.get("isOnlyChild");
        BigDecimal sharedRatio = data.get("sharedRatio") != null ? 
            new BigDecimal(data.get("sharedRatio").toString()) : new BigDecimal("100");

        if (isOnlyChild == null) {
            return BigDecimal.ZERO;
        }

        if (isOnlyChild == 1) {
            return ONLY_CHILD_AMOUNT;
        }

        BigDecimal amount = ONLY_CHILD_AMOUNT
                .multiply(sharedRatio)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        return amount.compareTo(NON_ONLY_CHILD_MAX) > 0 ? NON_ONLY_CHILD_MAX : amount;
    }

    @Override
    public boolean validate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer isOnlyChild = (Integer) data.get("isOnlyChild");
        Integer elderAge = (Integer) data.get("elderAge");

        if (isOnlyChild == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择是否为独生子女");
        }

        if (elderAge != null && elderAge < MIN_ELDER_AGE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "被赡养人年龄必须年满" + MIN_ELDER_AGE + "周岁");
        }

        BigDecimal sharedRatio = data.get("sharedRatio") != null ? 
            new BigDecimal(data.get("sharedRatio").toString()) : new BigDecimal("100");

        if (isOnlyChild == 0 && sharedRatio.compareTo(new BigDecimal("50")) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非独生子女分摊比例不能超过50%");
        }

        return true;
    }

    @Override
    public Integer getType() {
        return 6;
    }
}