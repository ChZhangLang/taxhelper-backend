package com.zihao.taxhelperai.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 个税计算结果VO
 *
 * @author 你的名字
 */
@Data
public class TaxCalculateVO {

    /**
     * 收入金额
     */
    private BigDecimal income;

    /**
     * 五险一金
     */
    private BigDecimal insurance;

    /**
     * 专项附加扣除
     */
    private BigDecimal deduct;

    /**
     * 应纳税所得额（计算过程值）
     */
    private BigDecimal taxableIncome;

    /**
     * 应缴税额
     */
    private BigDecimal taxAmount;

    /**
     * 计算类型 1-月薪 2-年度汇算
     */
    private Integer calcType;

    /**
     * 计算时间
     */
    private Date calcTime;
}