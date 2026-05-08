package com.zihao.taxhelperai.service.rule.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import com.zihao.taxhelperai.service.rule.DeductRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class SeriousIllnessRule implements DeductRule {

    private static final BigDecimal THRESHOLD = new BigDecimal("15000");
    private static final BigDecimal MAX_DEDUCTIBLE = new BigDecimal("100000");

    @Override
    public BigDecimal calculate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        BigDecimal selfPayExpense = data.get("selfPayExpense") != null ? 
            new BigDecimal(data.get("selfPayExpense").toString()) : BigDecimal.ZERO;

        BigDecimal deductible = selfPayExpense.subtract(THRESHOLD);

        if (deductible.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return deductible.compareTo(MAX_DEDUCTIBLE) > 0 ? MAX_DEDUCTIBLE : deductible;
    }

    @Override
    public boolean validate(DeductContext context) {
        Map<String, Object> data = context.getDetailData();
        Integer patientRelation = (Integer) data.get("patientRelation");
        BigDecimal selfPayExpense = data.get("selfPayExpense") != null ? 
            new BigDecimal(data.get("selfPayExpense").toString()) : BigDecimal.ZERO;

        if (patientRelation == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择患者与纳税人关系");
        }

        if (patientRelation < 1 || patientRelation > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "关系类型必须为1-本人、2-配偶或3-子女");
        }

        if (selfPayExpense.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "自费金额不能为负数");
        }

        return true;
    }

    @Override
    public Integer getType() {
        return 3;
    }
}