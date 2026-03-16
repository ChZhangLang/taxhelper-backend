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
import com.zihao.taxhelperai.model.vo.TaxCalculateVO;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;
import com.zihao.taxhelperai.service.SpecialDeductionService;
import com.zihao.taxhelperai.service.TaxRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 计税记录服务实现类
 *
 * @author 你的名字
 */
@Service
public class TaxRecordServiceImpl extends ServiceImpl<TaxRecordMapper, TaxRecord> implements TaxRecordService {

    // 个税起征点（月薪）
    private static final BigDecimal MONTHLY_THRESHOLD = new BigDecimal("5000");
    // 个税起征点（年度）
    private static final BigDecimal ANNUAL_THRESHOLD = new BigDecimal("60000");

    @Autowired
    private SpecialDeductionService specialDeductionService;

    @Override
    public TaxCalculateVO calculateAndSaveTax(TaxCalculateRequest taxCalculateRequest, Long userId) {
        // 1. 参数校验
        BigDecimal income = taxCalculateRequest.getIncome();
        BigDecimal insurance = taxCalculateRequest.getInsurance();
        Integer calcType = taxCalculateRequest.getCalcType();

        ThrowUtils.throwIf(income.compareTo(BigDecimal.ZERO) <= 0,
                            ErrorCode.PARAMS_ERROR, "收入金额必须大于0");
        ThrowUtils.throwIf(insurance.compareTo(BigDecimal.ZERO) < 0,
                            ErrorCode.PARAMS_ERROR, "五险一金不能为负数");
        ThrowUtils.throwIf(!calcType.equals(1) && !calcType.equals(2),
                            ErrorCode.PARAMS_ERROR, "计算类型只能是1（月薪）或2（年度汇算）");

        // 2. 从专项附加扣除模块获取用户的扣除总额
        BigDecimal deduct = specialDeductionService.getCurrentDeductionAmount(userId);
        if (deduct == null) {
            deduct = BigDecimal.ZERO;
        }

        // 3. 计算个税
        BigDecimal taxAmount;
        BigDecimal taxableIncome; // 应纳税所得额
        if (calcType.equals(1)) {
            // 月薪计算
            taxAmount = calculateMonthlyTax(income, insurance, deduct);
            // 计算应纳税所得额（展示用）
            taxableIncome = income.subtract(insurance).subtract(deduct).subtract(MONTHLY_THRESHOLD);
            if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                taxableIncome = BigDecimal.ZERO;
            }
        } else {
            // 年度汇算计算
            taxAmount = calculateAnnualTax(income, insurance, deduct);
            // 计算应纳税所得额（展示用）
            taxableIncome = income.subtract(insurance).subtract(deduct).subtract(ANNUAL_THRESHOLD);
            if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                taxableIncome = BigDecimal.ZERO;
            }
        }

        // 4. 保存计税记录
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

        // 5. 封装返回VO
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

//    @Override
//    public Page<TaxRecordVO> listTaxRecordVOByPage(TaxRecordQueryRequest taxRecordQueryRequest) {
//        // 1. 分页参数处理
//        long current = Math.max(taxRecordQueryRequest.getCurrent(), 1);
//        long size = Math.min(taxRecordQueryRequest.getPageSize(), 100);
//        Page<TaxRecord> taxRecordPage = this.page(new Page<>(current, size), getQueryWrapper(taxRecordQueryRequest));
//
//        // 2. 转换为VO分页对象
//        Page<TaxRecordVO> taxRecordVOPage = new Page<>(current, size, taxRecordPage.getTotal());
//        taxRecordVOPage.setRecords(taxRecordPage.getRecords().stream().
//                                    map(this::convertToTaxRecordVO).collect(Collectors.toList()));
//
//        return taxRecordVOPage;
//    }

    @Override
    public Page<TaxRecordVO> listTaxRecordVOByPage(TaxRecordQueryRequest taxRecordQueryRequest) {
        // 1. 分页参数处理（保留原有正确逻辑）
        long current = Math.max(taxRecordQueryRequest.getCurrent(), 1);
        long size = Math.min(taxRecordQueryRequest.getPageSize(), 100);
//        long size = Math.max(Math.min(taxRecordQueryRequest.getPageSize(), 100), 1);

        // 2. 执行分页查询（MyBatis-Plus 自动封装 total + records）
        Page<TaxRecord> taxRecordPage = this.page(new Page<>(current, size), getQueryWrapper(taxRecordQueryRequest));

        // 3. 核心修复：调用 VO 类的静态转换方法，完成批量转换
        Page<TaxRecordVO> taxRecordVOPage = (Page<TaxRecordVO>) taxRecordPage.convert(TaxRecordVO::objToVo);

        return taxRecordVOPage;
    }

    @Override
    public BigDecimal calculateMonthlyTax(BigDecimal income, BigDecimal insurance, BigDecimal deduct) {
        // 1. 计算应纳税所得额 = 收入 - 五险一金 - 专项附加扣除 - 5000起征点
        BigDecimal taxableIncome = income.subtract(insurance).subtract(deduct).subtract(MONTHLY_THRESHOLD);
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 2. 月薪个税税率表（超额累进）
        // 级数 应纳税所得额     税率(%)  速算扣除数
        // 1    不超过3000元     3       0
        // 2    超过3000至12000  10      210
        // 3    超过12000至25000 20      1410
        // 4    超过25000至35000 25      2660
        // 5    超过35000至55000 30      4410
        // 6    超过55000至80000 35      7160
        // 7    超过80000        45      15160
        BigDecimal tax = taxableIncome.multiply(getMonthlyTaxRate(taxableIncome))
                .subtract(getMonthlyQuickDeduction(taxableIncome));

        // 分布超额计算
//        if (taxable.compareTo(new BigDecimal("80000")) > 0) {
//            tax = tax.add(taxable.subtract(new BigDecimal("80000")).multiply(new BigDecimal("0.45")));
//            taxable = new BigDecimal("80000");
//        }
//        if (taxable.compareTo(new BigDecimal("55000")) > 0) {
//            tax = tax.add(taxable.subtract(new BigDecimal("55000")).multiply(new BigDecimal("0.35")));
//            taxable = new BigDecimal("55000");
//        }
//        if (taxable.compareTo(new BigDecimal("35000")) > 0) {
//            tax = tax.add(taxable.subtract(new BigDecimal("35000")).multiply(new BigDecimal("0.30")));
//            taxable = new BigDecimal("35000");
//        }
//        if (taxable.compareTo(new BigDecimal("25000")) > 0) {
//            tax = tax.add(taxable.subtract(new BigDecimal("25000")).multiply(new BigDecimal("0.25")));
//            taxable = new BigDecimal("25000");
//        }
//        if (taxable.compareTo(new BigDecimal("12000")) > 0) {
//            tax = tax.add(taxable.subtract(new BigDecimal("12000")).multiply(new BigDecimal("0.20")));
//            taxable = new BigDecimal("12000");
//        }
//        if (taxable.compareTo(new BigDecimal("3000")) > 0) {
//            tax = tax.add(taxable.subtract(new BigDecimal("3000")).multiply(new BigDecimal("0.10")));
//            taxable = new BigDecimal("3000");
//        }
//        tax = tax.add(taxable.multiply(new BigDecimal("0.03")));

        // 减去速算扣除数（简化写法，和上面分步计算结果一致）
        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateAnnualTax(BigDecimal income, BigDecimal insurance, BigDecimal deduct) {
        // 1. 计算年度应纳税所得额 = 年度收入 - 年度五险一金 - 年度专项附加扣除 - 60000起征点
        BigDecimal taxableIncome = income.subtract(insurance).subtract(deduct).subtract(ANNUAL_THRESHOLD);
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 2. 年度个税税率表（超额累进）
        // 级数 应纳税所得额       税率(%)  速算扣除数
        // 1    不超过36000元      3       0
        // 2    超过36000至144000  10      2520
        // 3    超过144000至300000 20      16920
        // 4    超过300000至420000 25      31920
        // 5    超过420000至660000 30      52920
        // 6    超过660000至960000 35      85920
        // 7    超过960000         45      181920
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

        // 减去速算扣除数
        tax = tax.subtract(getAnnualQuickDeduction(taxableIncome));

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<TaxRecord> getQueryWrapper(TaxRecordQueryRequest taxRecordQueryRequest) {
        QueryWrapper<TaxRecord> queryWrapper = new QueryWrapper<>();
        if (taxRecordQueryRequest == null) {
            return queryWrapper;
        }

        Long userId = taxRecordQueryRequest.getUserId();
        Integer calcType = taxRecordQueryRequest.getCalcType();

        // 拼接条件
        if (userId != null) {
            queryWrapper.eq("userId", userId);
        }
        if (calcType != null) {
            queryWrapper.eq("calcType", calcType);
        }

        // 按计算时间降序
        queryWrapper.orderByDesc("calcTime");

        return queryWrapper;
    }

//    /**
//     * 转换为TaxRecordVO
//     */
//    private TaxRecordVO convertToTaxRecordVO(TaxRecord taxRecord) {
//        TaxRecordVO taxRecordVO = new TaxRecordVO();
//        BeanUtils.copyProperties(taxRecord, taxRecordVO);
//        // 补充计算类型名称
//        if (Objects.equals(taxRecord.getCalcType(), 1)) {
//            taxRecordVO.setCalcTypeName("月薪");
//        } else if (Objects.equals(taxRecord.getCalcType(), 2)) {
//            taxRecordVO.setCalcTypeName("年度汇算");
//        } else {
//            taxRecordVO.setCalcTypeName("未知");
//        }
//        return taxRecordVO;
//    }

    // 根据应纳税所得额获取对应税率
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

    /**
     * 获取月薪个税速算扣除数
     */
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

    /**
     * 获取年度个税速算扣除数
     */
    private BigDecimal getAnnualQuickDeduction(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(new BigDecimal("36000")) <= 0) {
            return BigDecimal.ZERO;
        } else if (taxableIncome.compareTo(new BigDecimal("144000")) <= 0) {
            return new BigDecimal("2520");
        } else if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            return new BigDecimal("16920");
        } else if (taxableIncome.compareTo(new BigDecimal("420000")) <= 0) {
            return new BigDecimal("31920");
        } else if (taxableIncome.compareTo(new BigDecimal("660000")) <= 0) {
            return new BigDecimal("52920");
        } else if (taxableIncome.compareTo(new BigDecimal("960000")) <= 0) {
            return new BigDecimal("85920");
        } else {
            return new BigDecimal("181920");
        }
    }
}