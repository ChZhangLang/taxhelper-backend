package com.zihao.taxhelperai.service.rule;

import com.zihao.taxhelperai.model.context.DeductContext;

import java.math.BigDecimal;

public interface DeductRule {

    BigDecimal calculate(DeductContext context);

    boolean validate(DeductContext context);

    Integer getType();
}