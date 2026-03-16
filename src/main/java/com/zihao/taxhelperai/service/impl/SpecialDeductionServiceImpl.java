package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.mapper.SpecialDeductionMapper;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionAddRequest;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionEditRequest;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionQueryRequest;
import com.zihao.taxhelperai.model.entity.SpecialDeduction;
import com.zihao.taxhelperai.model.vo.SpecialDeductionVO;
import com.zihao.taxhelperai.service.SpecialDeductionService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专项附加扣除服务实现类
 */
@Service
public class SpecialDeductionServiceImpl extends ServiceImpl<SpecialDeductionMapper, SpecialDeduction> implements SpecialDeductionService {

    @Override
    public SpecialDeduction addSpecialDeduction(SpecialDeductionAddRequest request, Long userId) {
        // 1. 参数校验
        Integer deductionType = request.getDeductionType();
        BigDecimal amount = request.getAmount();
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();

        ThrowUtils.throwIf(deductionType == null || (deductionType < 1 || deductionType > 6),
                ErrorCode.PARAMS_ERROR, "扣除类型无效");
        ThrowUtils.throwIf(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0,
                ErrorCode.PARAMS_ERROR, "扣除金额必须大于0");
        ThrowUtils.throwIf(startDate == null || endDate == null,
                ErrorCode.PARAMS_ERROR, "开始日期和结束日期不能为空");
        ThrowUtils.throwIf(startDate.after(endDate),
                ErrorCode.PARAMS_ERROR, "开始日期不能晚于结束日期");

        // 2. 计算状态
        Integer status = calculateStatus(startDate, endDate);

        // 3. 创建专项附加扣除记录
        SpecialDeduction specialDeduction = new SpecialDeduction();
        specialDeduction.setUserId(userId);
        specialDeduction.setDeductionType(deductionType);
        specialDeduction.setAmount(amount);
        specialDeduction.setStartDate(startDate);
        specialDeduction.setEndDate(endDate);
        specialDeduction.setStatus(status);
        specialDeduction.setCreateTime(new Date());
        specialDeduction.setUpdateTime(new Date());
        specialDeduction.setIsDelete(0);

        // 4. 保存记录
        boolean saveResult = this.save(specialDeduction);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "保存专项附加扣除失败");

        return specialDeduction;
    }

    @Override
    public SpecialDeduction editSpecialDeduction(SpecialDeductionEditRequest request, Long userId) {
        // 1. 参数校验
        Long id = request.getId();
        Integer deductionType = request.getDeductionType();
        BigDecimal amount = request.getAmount();
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();

        ThrowUtils.throwIf(id == null,
                ErrorCode.PARAMS_ERROR, "扣除记录ID不能为空");
        ThrowUtils.throwIf(deductionType == null || (deductionType < 1 || deductionType > 6),
                ErrorCode.PARAMS_ERROR, "扣除类型无效");
        ThrowUtils.throwIf(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0,
                ErrorCode.PARAMS_ERROR, "扣除金额必须大于0");
        ThrowUtils.throwIf(startDate == null || endDate == null,
                ErrorCode.PARAMS_ERROR, "开始日期和结束日期不能为空");
        ThrowUtils.throwIf(startDate.after(endDate),
                ErrorCode.PARAMS_ERROR, "开始日期不能晚于结束日期");

        // 2. 查询记录是否存在且属于当前用户
        SpecialDeduction specialDeduction = this.getById(id);
        ThrowUtils.throwIf(specialDeduction == null,
                ErrorCode.NOT_FOUND_ERROR, "扣除记录不存在");
        ThrowUtils.throwIf(!specialDeduction.getUserId().equals(userId),
                ErrorCode.NO_AUTH_ERROR, "无权限操作此记录");

        // 3. 计算状态
        Integer status = calculateStatus(startDate, endDate);

        // 4. 更新记录
        specialDeduction.setDeductionType(deductionType);
        specialDeduction.setAmount(amount);
        specialDeduction.setStartDate(startDate);
        specialDeduction.setEndDate(endDate);
        specialDeduction.setStatus(status);
        specialDeduction.setUpdateTime(new Date());

        // 5. 保存更新
        boolean updateResult = this.updateById(specialDeduction);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新专项附加扣除失败");

        return specialDeduction;
    }

    @Override
    public boolean deleteSpecialDeduction(Long id, Long userId) {
        // 1. 参数校验
        ThrowUtils.throwIf(id == null,
                ErrorCode.PARAMS_ERROR, "扣除记录ID不能为空");

        // 2. 查询记录是否存在且属于当前用户
        SpecialDeduction specialDeduction = this.getById(id);
        ThrowUtils.throwIf(specialDeduction == null,
                ErrorCode.NOT_FOUND_ERROR, "扣除记录不存在");
        ThrowUtils.throwIf(!specialDeduction.getUserId().equals(userId),
                ErrorCode.NO_AUTH_ERROR, "无权限操作此记录");

        // 3. 软删除记录
        specialDeduction.setIsDelete(1);
        specialDeduction.setUpdateTime(new Date());

        return this.updateById(specialDeduction);
    }

    @Override
    public SpecialDeductionVO getSpecialDeductionById(Long id, Long userId) {
        // 1. 参数校验
        ThrowUtils.throwIf(id == null,
                ErrorCode.PARAMS_ERROR, "扣除记录ID不能为空");

        // 2. 查询记录是否存在且属于当前用户
        SpecialDeduction specialDeduction = this.getById(id);
        ThrowUtils.throwIf(specialDeduction == null || specialDeduction.getIsDelete() == 1,
                ErrorCode.NOT_FOUND_ERROR, "扣除记录不存在");
        ThrowUtils.throwIf(!specialDeduction.getUserId().equals(userId),
                ErrorCode.NO_AUTH_ERROR, "无权限查看此记录");

        // 3. 转换为VO
        return SpecialDeductionVO.objToVo(specialDeduction);
    }

    @Override
    public Page<SpecialDeductionVO> listSpecialDeductionVOByPage(SpecialDeductionQueryRequest request, Long userId) {
        // 1. 分页参数处理
        long current = Math.max(request.getCurrent(), 1);
        long size = Math.min(request.getPageSize(), 100);

        // 2. 构建查询条件
        QueryWrapper<SpecialDeduction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("isDelete", 0);

        // 3. 添加查询条件
        if (request.getDeductionType() != null) {
            queryWrapper.eq("deductionType", request.getDeductionType());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq("status", request.getStatus());
        }

        // 4. 按创建时间降序
        queryWrapper.orderByDesc("createTime");

        // 5. 执行分页查询
        Page<SpecialDeduction> specialDeductionPage = this.page(new Page<>(current, size), queryWrapper);

        // 6. 转换为VO分页对象
        Page<SpecialDeductionVO> specialDeductionVOPage = new Page<>(current, size, specialDeductionPage.getTotal());
        specialDeductionVOPage.setRecords(specialDeductionPage.getRecords().stream()
                .map(SpecialDeductionVO::objToVo)
                .collect(Collectors.toList()));

        return specialDeductionVOPage;
    }

    @Override
    public BigDecimal getCurrentDeductionAmount(Long userId) {
        // 1. 构建查询条件
        QueryWrapper<SpecialDeduction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("isDelete", 0);
        queryWrapper.eq("status", 1); // 只查询生效中的记录

        // 2. 计算总额
        List<SpecialDeduction> deductions = this.list(queryWrapper);
        return deductions.stream()
                .map(SpecialDeduction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void updateDeductionStatus() {
        // 1. 查询所有未删除的记录
        QueryWrapper<SpecialDeduction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        List<SpecialDeduction> deductions = this.list(queryWrapper);

        // 2. 更新状态
        for (SpecialDeduction deduction : deductions) {
            Integer newStatus = calculateStatus(deduction.getStartDate(), deduction.getEndDate());
            if (!newStatus.equals(deduction.getStatus())) {
                deduction.setStatus(newStatus);
                deduction.setUpdateTime(new Date());
                this.updateById(deduction);
            }
        }
    }

    /**
     * 计算扣除状态
     */
    private Integer calculateStatus(Date startDate, Date endDate) {
        Date now = new Date();
        if (now.before(startDate)) {
            return 0; // 未生效
        } else if (now.after(endDate)) {
            return 2; // 已过期
        } else {
            return 1; // 生效中
        }
    }
}
