package com.zihao.taxhelperai.service.rule;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DeductRuleEngine {

    private final Map<Integer, DeductRule> ruleMap;

    @Autowired
    public DeductRuleEngine(Map<Integer, DeductRule> ruleMap) {
        this.ruleMap = ruleMap;
    }

    public BigDecimal executeCalculate(Integer deductType, DeductContext context) {
        DeductRule rule = ruleMap.get(deductType);
        if (rule == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的扣除类型: " + deductType);
        }
        return rule.calculate(context);
    }

    public void executeValidate(Integer deductType, DeductContext context) {
        DeductRule rule = ruleMap.get(deductType);
        if (rule != null) {
            rule.validate(context);
        }
    }

    public BigDecimal executeCalculateWithValidate(Integer deductType, DeductContext context) {
        executeValidate(deductType, context);
        return executeCalculate(deductType, context);
    }
}