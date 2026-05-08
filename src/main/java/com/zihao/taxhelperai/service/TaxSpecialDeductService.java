package com.zihao.taxhelperai.service;

import com.zihao.taxhelperai.model.dto.specialDeduct.*;
import com.zihao.taxhelperai.model.entity.*;
import com.zihao.taxhelperai.model.vo.TaxSpecialDeductVO;

import java.math.BigDecimal;
import java.util.List;

public interface TaxSpecialDeductService {

    TaxSpecialDeduct addChildEducation(Long userId, ChildEducationDTO dto);

    TaxSpecialDeduct addHouseLoan(Long userId, HouseLoanDTO dto);

    TaxSpecialDeduct addHouseRent(Long userId, HouseRentDTO dto);

    TaxSpecialDeduct addElderSupport(Long userId, ElderSupportDTO dto);

    TaxSpecialDeduct addContinueEducation(Long userId, ContinueEducationDTO dto);

    TaxSpecialDeduct addSeriousIllness(Long userId, SeriousIllnessDTO dto);

    boolean deleteDeduct(Long id, Long userId);

    TaxSpecialDeduct getById(Long id);

    List<TaxSpecialDeductVO> listByUserId(Long userId);

    BigDecimal getCurrentDeductAmount(Long userId);

    /**
     * 获取用户在指定年月的专项附加扣除金额
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 扣除金额
     */
    BigDecimal getDeductAmountByYearMonth(Long userId, Integer year, Integer month);
}