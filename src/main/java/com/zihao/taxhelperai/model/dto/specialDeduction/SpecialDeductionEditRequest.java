package com.zihao.taxhelperai.model.dto.specialDeduction;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 专项附加扣除编辑请求
 */
@Data
public class SpecialDeductionEditRequest {
    /**
     * 主键
     */
    private Long id;

    /**
     * 扣除类型：1-子女教育 2-继续教育 3-大病医疗 4-住房贷款 5-住房租金 6-赡养老人
     */
    private Integer deductionType;

    /**
     * 扣除金额
     */
    private BigDecimal amount;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 扣除状态：0-未生效 1-生效中 2-已过期
     */
    private Integer status;
}
