package com.zihao.taxhelperai.model.dto.specialDeduction;

import com.zihao.taxhelperai.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专项附加扣除查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpecialDeductionQueryRequest extends PageRequest {
    /**
     * 扣除类型：1-子女教育 2-继续教育 3-大病医疗 4-住房贷款 5-住房租金 6-赡养老人
     */
    private Integer deductionType;

    /**
     * 扣除状态：0-未生效 1-生效中 2-已过期
     */
    private Integer status;
}
