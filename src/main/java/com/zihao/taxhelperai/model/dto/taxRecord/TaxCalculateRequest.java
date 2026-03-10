package com.zihao.taxhelperai.model.dto.taxRecord;

import lombok.Data;
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
}