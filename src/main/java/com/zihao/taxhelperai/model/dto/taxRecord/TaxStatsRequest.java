package com.zihao.taxhelperai.model.dto.taxRecord;

import lombok.Data;

/**
 * 税收统计分析请求DTO
 *
 * @author 你的名字
 */
@Data
public class TaxStatsRequest {

    /**
     * 用户ID（可选，筛选特定用户）
     */
    private Long userId;

    /**
     * 年份（可选）
     */
    private Integer year;

    /**
     * 月份（可选）
     */
    private Integer month;
}