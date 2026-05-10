package com.zihao.taxhelperai.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 税收统计数据VO
 *
 * @author 你的名字
 */
@Data
public class TaxStatsVO implements Serializable {

    /**
     * 总记录数
     */
    private Long totalCount;

    /**
     * 本月总税额
     */
    private BigDecimal currentMonthTax;

    /**
     * 本年总税额
     */
    private BigDecimal currentYearTax;

    /**
     * 按用户维度汇总
     */
    private List<UserTaxSummary> userSummaryList;

    /**
     * 按月份维度汇总
     */
    private List<MonthTaxSummary> monthSummaryList;

    /**
     * 用户税收汇总
     */
    @Data
    public static class UserTaxSummary implements Serializable {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户名称
         */
        private String userName;

        /**
         * 记录数量
         */
        private Long recordCount;

        /**
         * 总税额
         */
        private BigDecimal totalTax;
    }

    /**
     * 月份税收汇总
     */
    @Data
    public static class MonthTaxSummary implements Serializable {
        /**
         * 年份
         */
        private Integer year;

        /**
         * 月份
         */
        private Integer month;

        /**
         * 记录数量
         */
        private Long recordCount;

        /**
         * 总税额
         */
        private BigDecimal totalTax;
    }

    /**
     * 城市税收汇总
     */
    @Data
    public static class CityTaxSummary implements Serializable {
        /**
         * 城市名称
         */
        private String city;

        /**
         * 用户数量
         */
        private Long userCount;

        /**
         * 总税额
         */
        private BigDecimal totalTax;
    }
}