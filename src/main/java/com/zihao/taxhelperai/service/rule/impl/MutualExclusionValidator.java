package com.zihao.taxhelperai.service.rule.impl;

import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.entity.TaxSpecialDeduct;
import com.zihao.taxhelperai.mapper.TaxSpecialDeductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class MutualExclusionValidator {

    private static final int HOUSE_LOAN_TYPE = 4;
    private static final int HOUSE_RENT_TYPE = 5;

    private final TaxSpecialDeductMapper taxSpecialDeductMapper;

    @Autowired
    public MutualExclusionValidator(TaxSpecialDeductMapper taxSpecialDeductMapper) {
        this.taxSpecialDeductMapper = taxSpecialDeductMapper;
    }

    public void validateHouseMutualExclusion(Long userId, Integer newType, Date startDate, Date endDate) {
        if (newType != HOUSE_LOAN_TYPE && newType != HOUSE_RENT_TYPE) {
            return;
        }

        List<TaxSpecialDeduct> existingDeductions = taxSpecialDeductMapper.selectActiveByTypes(
                userId, Arrays.asList(HOUSE_LOAN_TYPE, HOUSE_RENT_TYPE));

        for (TaxSpecialDeduct existing : existingDeductions) {
            if (existing.getDeductType().equals(newType)) {
                continue;
            }

            if (isDateOverlap(startDate, endDate, existing.getStartDate(), existing.getEndDate())) {
                String newTypeName = newType == HOUSE_LOAN_TYPE ? "住房贷款利息" : "住房租金";
                String existingTypeName = existing.getDeductType() == HOUSE_LOAN_TYPE ? "住房贷款利息" : "住房租金";
                throw new BusinessException(ErrorCode.PARAMS_ERROR, 
                    newTypeName + "与" + existingTypeName + "不可同时享受");
            }
        }
    }

    private boolean isDateOverlap(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && start2.before(end1);
    }
}