package com.zihao.taxhelperai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionAddRequest;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionEditRequest;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionQueryRequest;
import com.zihao.taxhelperai.model.entity.SpecialDeduction;
import com.zihao.taxhelperai.model.vo.SpecialDeductionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.math.BigDecimal;

/**
 * 专项附加扣除服务
 */
public interface SpecialDeductionService extends IService<SpecialDeduction> {
    /**
     * 添加专项附加扣除
     */
    SpecialDeduction addSpecialDeduction(SpecialDeductionAddRequest request, Long userId);

    /**
     * 编辑专项附加扣除
     */
    SpecialDeduction editSpecialDeduction(SpecialDeductionEditRequest request, Long userId);

    /**
     * 删除专项附加扣除
     */
    boolean deleteSpecialDeduction(Long id, Long userId);

    /**
     * 获取专项附加扣除详情
     */
    SpecialDeductionVO getSpecialDeductionById(Long id, Long userId);

    /**
     * 分页查询专项附加扣除列表
     */
    Page<SpecialDeductionVO> listSpecialDeductionVOByPage(SpecialDeductionQueryRequest request, Long userId);

    /**
     * 获取用户当前生效的专项附加扣除总额
     */
    BigDecimal getCurrentDeductionAmount(Long userId);

    /**
     * 更新专项附加扣除状态
     */
    void updateDeductionStatus();
}
