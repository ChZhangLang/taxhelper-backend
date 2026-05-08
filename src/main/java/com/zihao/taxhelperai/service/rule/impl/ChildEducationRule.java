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
public class ChildEducationRule implements DeductRule {

    private static final BigDecimal MONTHLY_AMOUNT_PER_CHILD = new BigDecimal("2000");

    private static final int MAX_CHILD_COUNT = 3;

    @Override
    public BigDecimal calculate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer childCount = (Integer) data.get("childCount");
        BigDecimal sharedRatio = data.get("sharedRatio") != null ? 
            new BigDecimal(data.get("sharedRatio").toString()) : new BigDecimal("100");

        if (childCount == null || childCount <= 0) {
            return BigDecimal.ZERO;
        }

        return MONTHLY_AMOUNT_PER_CHILD
                .multiply(new BigDecimal(childCount))
                .multiply(sharedRatio)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean validate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer childCount = (Integer) data.get("childCount");

        if (childCount != null && childCount > MAX_CHILD_COUNT) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "子女数量不能超过" + MAX_CHILD_COUNT + "人");
        }

        BigDecimal sharedRatio = data.get("sharedRatio") != null ? 
            new BigDecimal(data.get("sharedRatio").toString()) : new BigDecimal("100");

        if (sharedRatio.compareTo(BigDecimal.ZERO) < 0 || sharedRatio.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分摊比例必须在0-100之间");
        }

        return true;
    }

    @Override
    public Integer getType() {
        return 1;
    }
}