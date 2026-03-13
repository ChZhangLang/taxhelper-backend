package com.zihao.taxhelperai.model.dto.policy;

import com.zihao.taxhelperai.common.PageRequest;
import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * @Author: 张梓豪
 * @CreateTime: 2026-03-13
 */

@Data
public class PolicyQueryDTO extends PageRequest {
    /** 政策类型 1-基础政策 2-扣除标准 3-申报流程（可选） */
    @Min(1)
    @Max(3)
    private Byte type;
    /** 政策标题关键词（可选） */
    @Size(max = 50, message = "关键词长度不能超过50")
    private String keyword;
}
