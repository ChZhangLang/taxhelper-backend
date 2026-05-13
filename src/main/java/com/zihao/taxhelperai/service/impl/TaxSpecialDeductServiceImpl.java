package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.model.context.DeductContext;
import com.zihao.taxhelperai.model.dto.specialDeduct.*;
import com.zihao.taxhelperai.model.entity.*;
import com.zihao.taxhelperai.model.vo.TaxSpecialDeductVO;
import com.zihao.taxhelperai.mapper.*;
import com.zihao.taxhelperai.service.TaxSpecialDeductService;
import com.zihao.taxhelperai.service.rule.DeductRuleEngine;
import com.zihao.taxhelperai.service.rule.impl.MutualExclusionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaxSpecialDeductServiceImpl extends ServiceImpl<TaxSpecialDeductMapper, TaxSpecialDeduct> implements TaxSpecialDeductService {

    private final DeductRuleEngine deductRuleEngine;
    private final MutualExclusionValidator mutualExclusionValidator;
    private final DeductChildEducationMapper childEducationMapper;
    private final DeductHouseLoanMapper houseLoanMapper;
    private final DeductHouseRentMapper houseRentMapper;
    private final DeductElderSupportMapper elderSupportMapper;
    private final DeductContinueEducationMapper continueEducationMapper;
    private final DeductSeriousIllnessMapper seriousIllnessMapper;

    @Autowired
    public TaxSpecialDeductServiceImpl(DeductRuleEngine deductRuleEngine,
                                       MutualExclusionValidator mutualExclusionValidator,
                                       DeductChildEducationMapper childEducationMapper,
                                       DeductHouseLoanMapper houseLoanMapper,
                                       DeductHouseRentMapper houseRentMapper,
                                       DeductElderSupportMapper elderSupportMapper,
                                       DeductContinueEducationMapper continueEducationMapper,
                                       DeductSeriousIllnessMapper seriousIllnessMapper) {
        this.deductRuleEngine = deductRuleEngine;
        this.mutualExclusionValidator = mutualExclusionValidator;
        this.childEducationMapper = childEducationMapper;
        this.houseLoanMapper = houseLoanMapper;
        this.houseRentMapper = houseRentMapper;
        this.elderSupportMapper = elderSupportMapper;
        this.continueEducationMapper = continueEducationMapper;
        this.seriousIllnessMapper = seriousIllnessMapper;
    }

    @Override
    @Transactional
    public TaxSpecialDeduct addChildEducation(Long userId, ChildEducationDTO dto) {
        DeductContext context = new DeductContext();
        context.setUserId(userId);
        context.setDeductType(1);
        context.put("childCount", dto.getChildCount() != null ? dto.getChildCount() : 1);
        context.put("isShared", dto.getIsShared());
        context.put("sharedRatio", dto.getSharedRatio() != null ? dto.getSharedRatio() : new BigDecimal("100"));

        deductRuleEngine.executeValidate(1, context);
        BigDecimal amount = deductRuleEngine.executeCalculate(1, context);

        return saveDeductWithDetail(userId, 1, dto.getStartDate(), dto.getEndDate(), amount, dto, null, null, null, null, null);
    }

    @Override
    @Transactional
    public TaxSpecialDeduct addHouseLoan(Long userId, HouseLoanDTO dto) {
        mutualExclusionValidator.validateHouseMutualExclusion(userId, 4, dto.getStartDate(), dto.getEndDate());

        DeductContext context = new DeductContext();
        context.setUserId(userId);
        context.setDeductType(4);
        context.put("isFirstHouse", dto.getIsFirstHouse());
        context.put("totalMonths", dto.getTotalMonths());

        deductRuleEngine.executeValidate(4, context);
        BigDecimal amount = deductRuleEngine.executeCalculate(4, context);

        return saveDeductWithDetail(userId, 4, dto.getStartDate(), dto.getEndDate(), amount, null, dto, null, null, null, null);
    }

    @Override
    @Transactional
    public TaxSpecialDeduct addHouseRent(Long userId, HouseRentDTO dto) {
        mutualExclusionValidator.validateHouseMutualExclusion(userId, 5, dto.getStartDate(), dto.getEndDate());

        DeductContext context = new DeductContext();
        context.setUserId(userId);
        context.setDeductType(5);
        context.put("cityLevel", dto.getCityLevel());
        context.put("hasHouseInCity", dto.getHasHouseInCity());

        deductRuleEngine.executeValidate(5, context);
        BigDecimal amount = deductRuleEngine.executeCalculate(5, context);

        return saveDeductWithDetail(userId, 5, dto.getStartDate(), dto.getEndDate(), amount, null, null, dto, null, null, null);
    }

    @Override
    @Transactional
    public TaxSpecialDeduct addElderSupport(Long userId, ElderSupportDTO dto) {
        DeductContext context = new DeductContext();
        context.setUserId(userId);
        context.setDeductType(6);
        context.put("isOnlyChild", dto.getIsOnlyChild());
        context.put("elderAge", dto.getElderAge());
        context.put("sharedRatio", dto.getSharedRatio() != null ? dto.getSharedRatio() : new BigDecimal("100"));

        deductRuleEngine.executeValidate(6, context);
        BigDecimal amount = deductRuleEngine.executeCalculate(6, context);

        return saveDeductWithDetail(userId, 6, dto.getStartDate(), dto.getEndDate(), amount, null, null, null, dto, null, null);
    }

    @Override
    @Transactional
    public TaxSpecialDeduct addContinueEducation(Long userId, ContinueEducationDTO dto) {
        DeductContext context = new DeductContext();
        context.setUserId(userId);
        context.setDeductType(2);
        context.put("educationType", dto.getEducationType());

        deductRuleEngine.executeValidate(2, context);
        BigDecimal amount = deductRuleEngine.executeCalculate(2, context);

        return saveDeductWithDetail(userId, 2, dto.getStartDate(), dto.getEndDate(), amount, null, null, null, null, dto, null);
    }

    @Override
    @Transactional
    public TaxSpecialDeduct addSeriousIllness(Long userId, SeriousIllnessDTO dto) {
        DeductContext context = new DeductContext();
        context.setUserId(userId);
        context.setDeductType(3);
        context.put("patientRelation", dto.getPatientRelation());
        context.put("selfPayExpense", dto.getSelfPayExpense());

        deductRuleEngine.executeValidate(3, context);
        BigDecimal amount = deductRuleEngine.executeCalculate(3, context);

        // 将费用发生年份转换为具体的日期范围（当年1月1日到12月31日）
        Date startDate = dto.getStartDate();
        Date endDate = dto.getEndDate();
        
        if (dto.getYear() != null && startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(dto.getYear(), 0, 1, 0, 0, 0);
            startDate = calendar.getTime();
            calendar.set(dto.getYear(), 11, 31, 23, 59, 59);
            endDate = calendar.getTime();
        }

        return saveDeductWithDetail(userId, 3, startDate, endDate, amount, null, null, null, null, null, dto);
    }

    private TaxSpecialDeduct saveDeductWithDetail(Long userId, Integer type, Date startDate, Date endDate, 
            BigDecimal amount, ChildEducationDTO childDTO, HouseLoanDTO loanDTO, HouseRentDTO rentDTO,
            ElderSupportDTO elderDTO, ContinueEducationDTO eduDTO, SeriousIllnessDTO illnessDTO) {
        
        TaxSpecialDeduct deduct = new TaxSpecialDeduct();
        deduct.setUserId(userId);
        deduct.setDeductType(type);
        deduct.setStartDate(startDate);
        deduct.setEndDate(endDate);
        deduct.setStatus(calculateStatus(startDate, endDate));
        deduct.setCreateTime(new Date());
        deduct.setUpdateTime(new Date());
        deduct.setIsDelete(0);
        this.save(deduct);

        switch (type) {
            case 1:
                saveChildEducation(deduct.getId(), childDTO, amount);
                break;
            case 2:
                saveContinueEducation(deduct.getId(), eduDTO, amount);
                break;
            case 3:
                saveSeriousIllness(deduct.getId(), illnessDTO, amount);
                break;
            case 4:
                saveHouseLoan(deduct.getId(), loanDTO, amount);
                break;
            case 5:
                saveHouseRent(deduct.getId(), rentDTO, amount);
                break;
            case 6:
                saveElderSupport(deduct.getId(), elderDTO, amount);
                break;
        }

        return deduct;
    }

    private void saveChildEducation(Long deductId, ChildEducationDTO dto, BigDecimal amount) {
        DeductChildEducation child = new DeductChildEducation();
        child.setDeductId(deductId);
        child.setChildName(dto.getChildName());
        child.setChildIdCard(dto.getChildIdCard());
        child.setEducationStage(dto.getEducationStage());
        child.setSchoolName(dto.getSchoolName());
        child.setIsShared(dto.getIsShared());
        child.setSharedRatio(dto.getSharedRatio() != null ? dto.getSharedRatio() : new BigDecimal("100"));
        child.setMonthlyAmount(amount);
        childEducationMapper.insert(child);
    }

    private void saveHouseLoan(Long deductId, HouseLoanDTO dto, BigDecimal amount) {
        DeductHouseLoan loan = new DeductHouseLoan();
        loan.setDeductId(deductId);
        loan.setHouseAddress(dto.getHouseAddress());
        loan.setIsFirstHouse(dto.getIsFirstHouse());
        loan.setLoanBank(dto.getLoanBank());
        loan.setLoanStartDate(dto.getLoanStartDate());
        loan.setTotalMonths(dto.getTotalMonths() != null ? dto.getTotalMonths() : 240);
        loan.setMonthlyAmount(amount);
        houseLoanMapper.insert(loan);
    }

    private void saveHouseRent(Long deductId, HouseRentDTO dto, BigDecimal amount) {
        DeductHouseRent rent = new DeductHouseRent();
        rent.setDeductId(deductId);
        rent.setRentAddress(dto.getRentAddress());
        rent.setCityLevel(dto.getCityLevel());
        rent.setHasHouseInCity(dto.getHasHouseInCity());
        rent.setMonthlyRent(dto.getMonthlyRent());
        rent.setMonthlyAmount(amount);
        houseRentMapper.insert(rent);
    }

    private void saveElderSupport(Long deductId, ElderSupportDTO dto, BigDecimal amount) {
        DeductElderSupport elder = new DeductElderSupport();
        elder.setDeductId(deductId);
        elder.setElderName(dto.getElderName());
        elder.setElderIdCard(dto.getElderIdCard());
        elder.setElderAge(dto.getElderAge());
        elder.setIsOnlyChild(dto.getIsOnlyChild());
        elder.setSharedCount(dto.getSharedCount() != null ? dto.getSharedCount() : 1);
        elder.setSharedRatio(dto.getSharedRatio() != null ? dto.getSharedRatio() : new BigDecimal("100"));
        elder.setMonthlyAmount(amount);
        elderSupportMapper.insert(elder);
    }

    private void saveContinueEducation(Long deductId, ContinueEducationDTO dto, BigDecimal amount) {
        DeductContinueEducation edu = new DeductContinueEducation();
        edu.setDeductId(deductId);
        edu.setEducationType(dto.getEducationType());
        edu.setEducationName(dto.getEducationName());
        edu.setInstitutionName(dto.getInstitutionName());
        edu.setCertificateNo(dto.getCertificateNo());
        edu.setMonthlyAmount(amount);
        continueEducationMapper.insert(edu);
    }

    private void saveSeriousIllness(Long deductId, SeriousIllnessDTO dto, BigDecimal amount) {
        DeductSeriousIllness illness = new DeductSeriousIllness();
        illness.setDeductId(deductId);
        illness.setPatientName(dto.getPatientName());
        illness.setPatientRelation(dto.getPatientRelation());
        illness.setTotalMedicalExpense(dto.getTotalMedicalExpense());
        illness.setInsuranceReimburse(dto.getInsuranceReimburse());
        illness.setSelfPayExpense(dto.getSelfPayExpense());
        illness.setDeductibleAmount(amount);
        illness.setYear(dto.getYear());
        seriousIllnessMapper.insert(illness);
    }

    @Override
    public boolean deleteDeduct(Long id, Long userId) {
        TaxSpecialDeduct deduct = this.getById(id);
        if (deduct == null || !deduct.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作此记录");
        }

        deduct.setIsDelete(1);
        deduct.setUpdateTime(new Date());
        return this.updateById(deduct);
    }

    @Override
    public TaxSpecialDeduct getById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<TaxSpecialDeductVO> listByUserId(Long userId) {
        QueryWrapper<TaxSpecialDeduct> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_delete", 0).orderByDesc("create_time");
        return this.list(wrapper).stream()
                .map(TaxSpecialDeductVO::objToVo)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getCurrentDeductAmount(Long userId) {
        QueryWrapper<TaxSpecialDeduct> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_delete", 0);
        List<TaxSpecialDeduct> deducts = this.list(wrapper);

        BigDecimal total = BigDecimal.ZERO;
        Date now = new Date();
        for (TaxSpecialDeduct deduct : deducts) {
            // 检查日期范围是否有效（当前日期在开始日期之后或相等，结束日期之前或未设置结束日期）
            boolean isEffective = false;
            if (deduct.getStartDate() != null && !now.before(deduct.getStartDate())) {
                if (deduct.getEndDate() == null || !now.after(deduct.getEndDate())) {
                    isEffective = true;
                }
            }
            if (isEffective) {
                BigDecimal amount = getMonthlyAmount(deduct.getId(), deduct.getDeductType());
                total = total.add(amount);
            }
        }
        return total;
    }

    @Override
    public BigDecimal getDeductAmountByYearMonth(Long userId, Integer year, Integer month) {
        QueryWrapper<TaxSpecialDeduct> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_delete", 0);
        List<TaxSpecialDeduct> deducts = this.list(wrapper);

        BigDecimal total = BigDecimal.ZERO;
        
        // 创建计税年月的日期（当月第一天）
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        Date targetDate = calendar.getTime();

        for (TaxSpecialDeduct deduct : deducts) {
            // 大病医疗（类型3）不在月度预扣预缴中扣除，仅在年度汇算时扣除
            if (deduct.getDeductType() == 3) {
                continue;
            }
            
            // 检查计税年月是否在扣除记录的日期范围内
            boolean isEffective = false;
            if (deduct.getStartDate() != null && !targetDate.before(deduct.getStartDate())) {
                if (deduct.getEndDate() == null || !targetDate.after(deduct.getEndDate())) {
                    isEffective = true;
                }
            }
            if (isEffective) {
                BigDecimal amount = getMonthlyAmount(deduct.getId(), deduct.getDeductType());
                total = total.add(amount);
            }
        }
        return total;
    }

    @Override
    public TaxSpecialDeduct getByUserIdAndYear(Long userId, Integer year) {
        // 查询用户所有有效的专项附加扣除记录
        QueryWrapper<TaxSpecialDeduct> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_delete", 0);
        List<TaxSpecialDeduct> deducts = this.list(wrapper);
        
        // 如果没有记录，返回null
        if (deducts == null || deducts.isEmpty()) {
            return null;
        }
        
        // 构建一个汇总的专项附加扣除信息
        TaxSpecialDeduct summary = new TaxSpecialDeduct();
        summary.setUserId(userId);
        
        // 汇总各类扣除金额
        java.math.BigDecimal childEducationAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal continueEducationAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal houseLoanAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal houseRentAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal elderSupportAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal seriousIllnessAmount = java.math.BigDecimal.ZERO;
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        Date yearStart = calendar.getTime();
        calendar.set(year, 11, 31);
        Date yearEnd = calendar.getTime();
        
        for (TaxSpecialDeduct deduct : deducts) {
            boolean isEffective = false;
            if (deduct.getStartDate() != null && !yearEnd.before(deduct.getStartDate())) {
                if (deduct.getEndDate() == null || !yearStart.after(deduct.getEndDate())) {
                    isEffective = true;
                }
            }
            
            if (isEffective) {
                java.math.BigDecimal amount = getMonthlyAmount(deduct.getId(), deduct.getDeductType());
                switch (deduct.getDeductType()) {
                    case 1:
                        childEducationAmount = childEducationAmount.add(amount);
                        break;
                    case 2:
                        continueEducationAmount = continueEducationAmount.add(amount);
                        break;
                    case 3:
                        seriousIllnessAmount = seriousIllnessAmount.add(amount);
                        break;
                    case 4:
                        houseLoanAmount = houseLoanAmount.add(amount);
                        break;
                    case 5:
                        houseRentAmount = houseRentAmount.add(amount);
                        break;
                    case 6:
                        elderSupportAmount = elderSupportAmount.add(amount);
                        break;
                }
            }
        }
        
        // 设置汇总金额到返回对象
        summary.setChildEducationAmount(childEducationAmount);
        summary.setContinueEducationAmount(continueEducationAmount);
        summary.setHouseLoanAmount(houseLoanAmount);
        summary.setHouseRentAmount(houseRentAmount);
        summary.setElderSupportAmount(elderSupportAmount);
        summary.setSeriousIllnessAmount(seriousIllnessAmount);
        
        return summary;
    }

    private BigDecimal getMonthlyAmount(Long deductId, Integer type) {
        switch (type) {
            case 1:
                DeductChildEducation child = childEducationMapper.selectByDeductId(deductId);
                return child != null ? child.getMonthlyAmount() : BigDecimal.ZERO;
            case 2:
                DeductContinueEducation edu = continueEducationMapper.selectByDeductId(deductId);
                return edu != null ? edu.getMonthlyAmount() : BigDecimal.ZERO;
            case 3:
                DeductSeriousIllness illness = seriousIllnessMapper.selectByDeductId(deductId);
                return illness != null ? illness.getDeductibleAmount() : BigDecimal.ZERO;
            case 4:
                DeductHouseLoan loan = houseLoanMapper.selectByDeductId(deductId);
                return loan != null ? loan.getMonthlyAmount() : BigDecimal.ZERO;
            case 5:
                DeductHouseRent rent = houseRentMapper.selectByDeductId(deductId);
                return rent != null ? rent.getMonthlyAmount() : BigDecimal.ZERO;
            case 6:
                DeductElderSupport elder = elderSupportMapper.selectByDeductId(deductId);
                return elder != null ? elder.getMonthlyAmount() : BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
    }

    private Integer calculateStatus(Date startDate, Date endDate) {
        Date now = new Date();
        // 如果开始日期为空，默认为立即生效
        if (startDate == null) {
            startDate = now;
        }
        if (now.before(startDate)) {
            return 0;
        } else if (endDate != null && now.after(endDate)) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public List<TaxSpecialDeductVO> getFilingRecordsByYear(Long userId, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        Date yearStart = calendar.getTime();
        calendar.set(year, 11, 31, 23, 59, 59);
        Date yearEnd = calendar.getTime();

        QueryWrapper<TaxSpecialDeduct> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("is_delete", 0)
               .orderByDesc("start_date");
        
        return this.list(wrapper).stream()
                .filter(deduct -> {
                    Date startDate = deduct.getStartDate();
                    Date endDate = deduct.getEndDate();
                    
                    if (startDate == null) {
                        startDate = deduct.getCreateTime();
                    }
                    
                    if (endDate == null) {
                        endDate = new Date(Long.MAX_VALUE);
                    }
                    
                    return !(endDate.before(yearStart) || startDate.after(yearEnd));
                })
                .map(deduct -> {
                    TaxSpecialDeductVO vo = TaxSpecialDeductVO.objToVo(deduct);
                    if (vo != null) {
                        vo.setAmount(getMonthlyAmount(deduct.getId(), deduct.getDeductType()));
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }
}