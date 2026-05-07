package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.mapper.TaxRecordMapper;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxCalculateRequest;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordQueryRequest;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.TaxCalculateVO;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;
import com.zihao.taxhelperai.model.vo.TaxStatsVO;
import com.zihao.taxhelperai.model.vo.TaxSettlementVO;
import com.zihao.taxhelperai.service.SpecialDeductionService;
import com.zihao.taxhelperai.service.TaxRecordService;
import com.zihao.taxhelperai.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计税记录服务实现类
 *
 * @author 你的名字
 */
@Service
public class TaxRecordServiceImpl extends ServiceImpl<TaxRecordMapper, TaxRecord> implements TaxRecordService {

    private static final BigDecimal MONTHLY_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal ANNUAL_THRESHOLD = new BigDecimal("60000");

    @Autowired
    private SpecialDeductionService specialDeductionService;

    @Autowired
    private UserService userService;

    @Override
    public TaxCalculateVO calculateAndSaveTax(TaxCalculateRequest taxCalculateRequest, Long userId) {
        BigDecimal income = taxCalculateRequest.getIncome();
        BigDecimal insurance = taxCalculateRequest.getInsurance();
        Integer calcType = taxCalculateRequest.getCalcType();

        ThrowUtils.throwIf(income.compareTo(BigDecimal.ZERO) <= 0,
                ErrorCode.PARAMS_ERROR, "收入金额必须大于0");
        ThrowUtils.throwIf(insurance.compareTo(BigDecimal.ZERO) < 0,
                ErrorCode.PARAMS_ERROR, "五险一金不能为负数");
        ThrowUtils.throwIf(!calcType.equals(1) && !calcType.equals(2),
                ErrorCode.PARAMS_ERROR, "计算类型只能是1（月薪）或2（年度汇算）");

        BigDecimal deduct = specialDeductionService.getCurrentDeductionAmount(userId);
        if (deduct == null) {
            deduct = BigDecimal.ZERO;
        }

        BigDecimal taxAmount;
        BigDecimal taxableIncome;
        if (calcType.equals(1)) {
            taxAmount = calculateMonthlyTax(income, insurance, deduct);
            taxableIncome = income.subtract(insurance).subtract(deduct).subtract(MONTHLY_THRESHOLD);
            if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                taxableIncome = BigDecimal.ZERO;
            }
        } else {
            taxAmount = calculateAnnualTax(income, insurance, deduct);
            taxableIncome = income.subtract(insurance).subtract(deduct).subtract(ANNUAL_THRESHOLD);
            if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                taxableIncome = BigDecimal.ZERO;
            }
        }

        TaxRecord taxRecord = new TaxRecord();
        taxRecord.setUserId(userId);
        taxRecord.setIncome(income);
        taxRecord.setInsurance(insurance);
        taxRecord.setDeduct(deduct);
        taxRecord.setTaxAmount(taxAmount);
        taxRecord.setCalcType(calcType);
        taxRecord.setCalcTime(new Date());
        taxRecord.setIsDelete(0);
        boolean saveResult = this.save(taxRecord);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "保存计税记录失败");

        TaxCalculateVO taxCalculateVO = new TaxCalculateVO();
        taxCalculateVO.setIncome(income);
        taxCalculateVO.setInsurance(insurance);
        taxCalculateVO.setDeduct(deduct);
        taxCalculateVO.setCalcType(calcType);
        taxCalculateVO.setTaxableIncome(taxableIncome.setScale(2, RoundingMode.HALF_UP));
        taxCalculateVO.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
        taxCalculateVO.setCalcTime(new Date());

        return taxCalculateVO;
    }

    @Override
    public TaxCalculateVO calculateMonthlyWithCumulative(TaxCalculateRequest request, Long userId) {
        Integer year = request.getYear();
        Integer month = request.getMonth();
        BigDecimal income = request.getIncome();
        BigDecimal insurance = request.getInsurance();
        BigDecimal deduct = request.getDeduct() != null ? request.getDeduct() : BigDecimal.ZERO;

        ThrowUtils.throwIf(income.compareTo(BigDecimal.ZERO) <= 0,
                ErrorCode.PARAMS_ERROR, "收入金额必须大于0");
        ThrowUtils.throwIf(insurance.compareTo(BigDecimal.ZERO) < 0,
                ErrorCode.PARAMS_ERROR, "五险一金不能为负数");

        BigDecimal cumulativeIncome = baseMapper.getCumulativeIncome(userId, year);
        BigDecimal cumulativeInsurance = baseMapper.getCumulativeInsurance(userId, year);
        BigDecimal cumulativeDeduct = baseMapper.getCumulativeDeduct(userId, year);
        BigDecimal cumulativeTax = baseMapper.getCumulativeTax(userId, year);

        if (cumulativeIncome == null) cumulativeIncome = BigDecimal.ZERO;
        if (cumulativeInsurance == null) cumulativeInsurance = BigDecimal.ZERO;
        if (cumulativeDeduct == null) cumulativeDeduct = BigDecimal.ZERO;
        if (cumulativeTax == null) cumulativeTax = BigDecimal.ZERO;

        BigDecimal totalIncome = cumulativeIncome.add(income);
        BigDecimal totalInsurance = cumulativeInsurance.add(insurance);
        BigDecimal totalDeduct = cumulativeDeduct.add(deduct);

        BigDecimal cumulativeDeduction = MONTHLY_THRESHOLD.multiply(new BigDecimal(month));
        BigDecimal taxableIncome = totalIncome.subtract(totalInsurance)
                .subtract(totalDeduct)
                .subtract(cumulativeDeduction);

        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        BigDecimal cumulativeTaxAmount = calculateCumulativeTax(taxableIncome);
        BigDecimal currentMonthTax = cumulativeTaxAmount.subtract(cumulativeTax);
        if (currentMonthTax.compareTo(BigDecimal.ZERO) < 0) {
            currentMonthTax = BigDecimal.ZERO;
        }

        TaxRecord record = new TaxRecord();
        record.setUserId(userId);
        record.setIncome(income);
        record.setInsurance(insurance);
        record.setDeduct(deduct);
        record.setTaxAmount(currentMonthTax);
        record.setCalcType(1);
        record.setCalcTime(new Date());
        record.setTaxYear(year);
        record.setTaxMonth(month);
        record.setCumulativeIncome(totalIncome);
        record.setCumulativeInsurance(totalInsurance);
        record.setCumulativeDeduct(totalDeduct);
        record.setCumulativeTax(cumulativeTaxAmount);
        record.setTaxableIncome(taxableIncome);
        record.setBeforeTaxIncome(income.subtract(insurance));
        record.setIsDelete(0);
        this.save(record);

        TaxCalculateVO vo = new TaxCalculateVO();
        vo.setIncome(income);
        vo.setInsurance(insurance);
        vo.setDeduct(deduct);
        vo.setCalcType(1);
        vo.setTaxableIncome(taxableIncome.setScale(2, RoundingMode.HALF_UP));
        vo.setTaxAmount(currentMonthTax.setScale(2, RoundingMode.HALF_UP));
        vo.setCalcTime(new Date());
        vo.setMonth(month);
        vo.setCumulativeIncome(totalIncome);
        vo.setCumulativeInsurance(totalInsurance);
        vo.setCumulativeDeduct(totalDeduct);
        vo.setCumulativeTax(cumulativeTaxAmount.setScale(2, RoundingMode.HALF_UP));
        vo.setAfterTaxIncome(income.subtract(insurance).subtract(currentMonthTax).setScale(2, RoundingMode.HALF_UP));

        return vo;
    }

    private BigDecimal calculateCumulativeTax(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal taxable = taxableIncome;

        if (taxable.compareTo(new BigDecimal("960000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("960000")).multiply(new BigDecimal("0.45")));
            taxable = new BigDecimal("960000");
        }
        if (taxable.compareTo(new BigDecimal("660000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("660000")).multiply(new BigDecimal("0.35")));
            taxable = new BigDecimal("660000");
        }
        if (taxable.compareTo(new BigDecimal("420000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("420000")).multiply(new BigDecimal("0.30")));
            taxable = new BigDecimal("420000");
        }
        if (taxable.compareTo(new BigDecimal("300000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("300000")).multiply(new BigDecimal("0.25")));
            taxable = new BigDecimal("300000");
        }
        if (taxable.compareTo(new BigDecimal("144000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("144000")).multiply(new BigDecimal("0.20")));
            taxable = new BigDecimal("144000");
        }
        if (taxable.compareTo(new BigDecimal("36000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("36000")).multiply(new BigDecimal("0.10")));
            taxable = new BigDecimal("36000");
        }
        tax = tax.add(taxable.multiply(new BigDecimal("0.03")));

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public TaxSettlementVO calculateAnnualSettlement(Long userId, Integer year) {
        List<TaxRecord> yearRecords = baseMapper.selectYearRecords(userId, year);

        if (yearRecords == null || yearRecords.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "年度无计税记录，无法进行汇算");
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalInsurance = BigDecimal.ZERO;
        BigDecimal totalDeduct = BigDecimal.ZERO;
        BigDecimal totalPaidTax = BigDecimal.ZERO;

        List<TaxSettlementVO.MonthlyDetail> monthlyDetails = new ArrayList<>();

        for (TaxRecord record : yearRecords) {
            totalIncome = totalIncome.add(record.getIncome());
            totalInsurance = totalInsurance.add(record.getInsurance());
            totalDeduct = totalDeduct.add(record.getDeduct());
            totalPaidTax = totalPaidTax.add(record.getTaxAmount());

            TaxSettlementVO.MonthlyDetail detail = new TaxSettlementVO.MonthlyDetail();
            detail.setMonth(record.getTaxMonth());
            detail.setIncome(record.getIncome());
            detail.setInsurance(record.getInsurance());
            detail.setDeduct(record.getDeduct());
            detail.setTax(record.getTaxAmount());
            if (record.getBeforeTaxIncome() != null) {
                detail.setAfterTaxIncome(record.getBeforeTaxIncome().subtract(record.getTaxAmount()));
            } else {
                detail.setAfterTaxIncome(record.getIncome().subtract(record.getInsurance()).subtract(record.getTaxAmount()));
            }
            monthlyDetails.add(detail);
        }

        BigDecimal annualTaxableIncome = totalIncome
                .subtract(totalInsurance)
                .subtract(totalDeduct)
                .subtract(ANNUAL_THRESHOLD);

        if (annualTaxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            annualTaxableIncome = BigDecimal.ZERO;
        }

        BigDecimal annualTaxAmount = calculateAnnualTax(totalIncome.subtract(totalInsurance).subtract(totalDeduct), BigDecimal.ZERO, BigDecimal.ZERO);

        BigDecimal settlementAmount = annualTaxAmount.subtract(totalPaidTax);

        TaxSettlementVO vo = new TaxSettlementVO();
        vo.setYear(year);
        vo.setTotalIncome(totalIncome);
        vo.setTotalInsurance(totalInsurance);
        vo.setTotalDeduct(totalDeduct);
        vo.setTotalPaidTax(totalPaidTax);
        vo.setAnnualTaxableIncome(annualTaxableIncome);
        vo.setAnnualTaxAmount(annualTaxAmount);
        vo.setSettlementAmount(settlementAmount.setScale(2, RoundingMode.HALF_UP));

        if (settlementAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setSettlementDesc("需补税 ¥" + settlementAmount.setScale(2, RoundingMode.HALF_UP));
        } else if (settlementAmount.compareTo(BigDecimal.ZERO) < 0) {
            vo.setSettlementDesc("可退税 ¥" + settlementAmount.abs().setScale(2, RoundingMode.HALF_UP));
        } else {
            vo.setSettlementDesc("无需补退");
        }

        vo.setMonthlyDetails(monthlyDetails);

        return vo;
    }

    @Override
    public Map<String, BigDecimal> getCumulativeData(Long userId, Integer year) {
        Map<String, BigDecimal> data = new HashMap<>();
        BigDecimal income = baseMapper.getCumulativeIncome(userId, year);
        BigDecimal insurance = baseMapper.getCumulativeInsurance(userId, year);
        BigDecimal deduct = baseMapper.getCumulativeDeduct(userId, year);
        BigDecimal tax = baseMapper.getCumulativeTax(userId, year);

        data.put("cumulativeIncome", income != null ? income : BigDecimal.ZERO);
        data.put("cumulativeInsurance", insurance != null ? insurance : BigDecimal.ZERO);
        data.put("cumulativeDeduct", deduct != null ? deduct : BigDecimal.ZERO);
        data.put("cumulativeTax", tax != null ? tax : BigDecimal.ZERO);

        return data;
    }

    @Override
    public Page<TaxRecordVO> listTaxRecordVOByPage(TaxRecordQueryRequest taxRecordQueryRequest) {
        long current = Math.max(taxRecordQueryRequest.getCurrent(), 1);
        long size = Math.min(taxRecordQueryRequest.getPageSize(), 100);

        Page<TaxRecord> taxRecordPage = this.page(new Page<>(current, size), getQueryWrapper(taxRecordQueryRequest));

        Page<TaxRecordVO> taxRecordVOPage = (Page<TaxRecordVO>) taxRecordPage.convert(record -> {
            TaxRecordVO vo = TaxRecordVO.objToVo(record);
            if (vo != null && vo.getUserId() != null) {
                User user = userService.getById(vo.getUserId());
                if (user != null) {
                    vo.setUserName(user.getRealName());
                }
            }
            return vo;
        });

        return taxRecordVOPage;
    }

    @Override
    public BigDecimal calculateMonthlyTax(BigDecimal income, BigDecimal insurance, BigDecimal deduct) {
        BigDecimal taxableIncome = income.subtract(insurance).subtract(deduct).subtract(MONTHLY_THRESHOLD);
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = taxableIncome.multiply(getMonthlyTaxRate(taxableIncome))
                .subtract(getMonthlyQuickDeduction(taxableIncome));

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateAnnualTax(BigDecimal income, BigDecimal insurance, BigDecimal deduct) {
        BigDecimal taxableIncome = income.subtract(insurance).subtract(deduct).subtract(ANNUAL_THRESHOLD);
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal taxable = taxableIncome;

        if (taxable.compareTo(new BigDecimal("960000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("960000")).multiply(new BigDecimal("0.45")));
            taxable = new BigDecimal("960000");
        }
        if (taxable.compareTo(new BigDecimal("660000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("660000")).multiply(new BigDecimal("0.35")));
            taxable = new BigDecimal("660000");
        }
        if (taxable.compareTo(new BigDecimal("420000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("420000")).multiply(new BigDecimal("0.30")));
            taxable = new BigDecimal("420000");
        }
        if (taxable.compareTo(new BigDecimal("300000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("300000")).multiply(new BigDecimal("0.25")));
            taxable = new BigDecimal("300000");
        }
        if (taxable.compareTo(new BigDecimal("144000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("144000")).multiply(new BigDecimal("0.20")));
            taxable = new BigDecimal("144000");
        }
        if (taxable.compareTo(new BigDecimal("36000")) > 0) {
            tax = tax.add(taxable.subtract(new BigDecimal("36000")).multiply(new BigDecimal("0.10")));
            taxable = new BigDecimal("36000");
        }
        tax = tax.add(taxable.multiply(new BigDecimal("0.03")));

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    private QueryWrapper<TaxRecord> getQueryWrapper(TaxRecordQueryRequest taxRecordQueryRequest) {
        QueryWrapper<TaxRecord> queryWrapper = new QueryWrapper<>();
        if (taxRecordQueryRequest == null) {
            return queryWrapper;
        }

        Long userId = taxRecordQueryRequest.getUserId();
        Integer calcType = taxRecordQueryRequest.getCalcType();
        Integer year = taxRecordQueryRequest.getYear();
        Integer month = taxRecordQueryRequest.getMonth();

        if (userId != null) {
            queryWrapper.eq("userId", userId);
        }
        if (calcType != null) {
            queryWrapper.eq("calcType", calcType);
        }
        if (year != null) {
            queryWrapper.eq("taxYear", year);
        }
        if (month != null) {
            queryWrapper.eq("taxMonth", month);
        }

        queryWrapper.orderByDesc("calcTime");

        return queryWrapper;
    }

    @Override
    public TaxStatsVO getTaxStats() {
        TaxStatsVO statsVO = new TaxStatsVO();
        statsVO.setTotalCount(baseMapper.selectTotalCount());
        statsVO.setCurrentMonthTax(baseMapper.selectCurrentMonthTax());
        statsVO.setCurrentYearTax(baseMapper.selectCurrentYearTax());
        statsVO.setUserSummaryList(baseMapper.selectUserTaxSummary());
        statsVO.setMonthSummaryList(baseMapper.selectMonthTaxSummary());
        return statsVO;
    }

    private BigDecimal getMonthlyTaxRate(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(new BigDecimal("3000")) <= 0) {
            return new BigDecimal("0.03");
        } else if (taxableIncome.compareTo(new BigDecimal("12000")) <= 0) {
            return new BigDecimal("0.10");
        } else if (taxableIncome.compareTo(new BigDecimal("25000")) <= 0) {
            return new BigDecimal("0.20");
        } else if (taxableIncome.compareTo(new BigDecimal("35000")) <= 0) {
            return new BigDecimal("0.25");
        } else if (taxableIncome.compareTo(new BigDecimal("55000")) <= 0) {
            return new BigDecimal("0.30");
        } else if (taxableIncome.compareTo(new BigDecimal("80000")) <= 0) {
            return new BigDecimal("0.35");
        } else {
            return new BigDecimal("0.45");
        }
    }

    private BigDecimal getMonthlyQuickDeduction(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(new BigDecimal("3000")) <= 0) {
            return BigDecimal.ZERO;
        } else if (taxableIncome.compareTo(new BigDecimal("12000")) <= 0) {
            return new BigDecimal("210");
        } else if (taxableIncome.compareTo(new BigDecimal("25000")) <= 0) {
            return new BigDecimal("1410");
        } else if (taxableIncome.compareTo(new BigDecimal("35000")) <= 0) {
            return new BigDecimal("2660");
        } else if (taxableIncome.compareTo(new BigDecimal("55000")) <= 0) {
            return new BigDecimal("4410");
        } else if (taxableIncome.compareTo(new BigDecimal("80000")) <= 0) {
            return new BigDecimal("7160");
        } else {
            return new BigDecimal("15160");
        }
    }
}