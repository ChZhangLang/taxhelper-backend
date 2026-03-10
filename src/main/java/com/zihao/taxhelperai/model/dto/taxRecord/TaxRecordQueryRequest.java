package com.zihao.taxhelperai.model.dto.taxRecord;

import com.zihao.taxhelperai.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 计税记录分页查询DTO
 *
 * @author 你的名字
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaxRecordQueryRequest extends PageRequest {

    /**
     * 用户ID（管理员查询时用）
     */
    private Long userId;

    /**
     * 计算类型 1-月薪 2-年度汇算
     */
    private Integer calcType;
}