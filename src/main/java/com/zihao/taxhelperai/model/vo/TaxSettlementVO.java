package com.zihao.taxhelperai.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 年度汇算清缴结果VO
 *
 * @author 张梓豪
 */
@Data
public class TaxSettlementVO {

    /**
     * 年度
     */
    private Integer year;

    /**
     * 全年总收入
     */
    private BigDecimal totalIncome;

    /**
     * 全年五险一金总额
     */
    private BigDecimal totalInsurance;

    /**
     * 全年专项附加扣除总额
     */
    private BigDecimal totalDeduct;

    /**
     * 全年已预扣税额
     */
    private BigDecimal totalPaidTax;

    /**
     * 年度应纳税所得额
     */
    private BigDecimal annualTaxableIncome;

    /**
     * 年度应纳税额
     */
    private BigDecimal annualTaxAmount;

    /**
     * 应补/退税额（正数：补税，负数：退税）
     */
    private BigDecimal settlementAmount;

    /**
     * 汇算结果描述
     */
    private String settlementDesc;

    /**
     * 月度明细列表
     */
    private List<MonthlyDetail> monthlyDetails;

    /**
     * 月度明细
     */
    @Data
    public static class MonthlyDetail {
        /**
         * 月份
         */
        private Integer month;

        /**
         * 收入
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
         * 已缴税
         */
        private BigDecimal tax;

        /**
         * 税后收入
         */
        private BigDecimal afterTaxIncome;
    }
}