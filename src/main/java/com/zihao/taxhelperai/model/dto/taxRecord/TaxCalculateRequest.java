package com.zihao.taxhelperai.model.dto.taxRecord;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 个税计算请求DTO
 *
 * @author 张梓豪
 */
@Data
public class TaxCalculateRequest {

    /**
     * 收入金额（必填）
     */
    @NotNull(message = "收入金额不能为空")
    private BigDecimal income;

    /**
     * 五险一金（非必填，默认0）
     */
    private BigDecimal insurance = BigDecimal.ZERO;

    /**
     * 专项附加扣除（非必填，默认0）
     */
    private BigDecimal deduct = BigDecimal.ZERO;

    /**
     * 计算类型 1-月薪 2-年度汇算（必填）
     */
    @NotNull(message = "计算类型不能为空")
    private Integer calcType;

    /**
     * 计税年份
     */
    @NotNull(message = "年份不能为空")
    private Integer year;

    /**
     * 计税月份（1-12）
     */
    @NotNull(message = "月份不能为空")
    @Min(value = 1, message = "月份最小为1")
    @Max(value = 12, message = "月份最大为12")
    private Integer month;
}