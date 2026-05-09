package com.zihao.taxhelperai.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class AnalysisVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Data
    public static class AnnualSummary implements Serializable {
        private Integer taxYear;
        private BigDecimal totalIncome;
        private BigDecimal totalTax;
        private BigDecimal totalDeduct;
        private BigDecimal averageTaxRate;
        private BigDecimal lastYearIncome;
        private BigDecimal lastYearTax;
        private BigDecimal incomeGrowth;
        private BigDecimal taxGrowth;
    }

    @Data
    public static class MonthlyTrend implements Serializable {
        private Integer month;
        private String monthName;
        private BigDecimal income;
        private BigDecimal tax;
        private BigDecimal deduct;
        private BigDecimal beforeTaxIncome;
    }

    @Data
    public static class DeductRatio implements Serializable {
        private String deductType;
        private String deductTypeName;
        private BigDecimal amount;
        private BigDecimal ratio;
        private Long count;
    }

    @Data
    public static class TaxAnalysisReport implements Serializable {
        private String reportDate;
        private BigDecimal totalIncome;
        private BigDecimal totalTax;
        private BigDecimal effectiveTaxRate;
        private String taxLevel;
        private List<String> suggestions;
        private String summary;
    }

    @Data
    public static class PlatformStats implements Serializable {
        private Long totalUsers;
        private Long todayNewUsers;
        private BigDecimal totalTaxAmount;
        private BigDecimal avgTaxPerUser;
        private Long activeUsers;
        private Long totalRecords;
        private BigDecimal avgDeductAmount;
        private BigDecimal deductUsageRate;
    }

    @Data
    public static class IncomeDistribution implements Serializable {
        private String incomeRange;
        private Long userCount;
        private BigDecimal proportion;
    }

    @Data
    public static class PolicyStats implements Serializable {
        private String policyTitle;
        private Long viewCount;
        private Long clickCount;
    }

    @Data
    public static class UserBehaviorStats implements Serializable {
        private String date;
        private Long loginCount;
        private Long queryCount;
        private Long calculateCount;
        private Long deductUpdateCount;
    }

    @Data
    public static class CityTaxStats implements Serializable {
        private String city;
        private BigDecimal totalTax;
        private Long userCount;
        private BigDecimal avgTax;
    }
}
