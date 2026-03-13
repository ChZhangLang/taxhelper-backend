package com.zihao.taxhelperai.model.dto.guide;

/**
 * @Author: 张梓豪
 * @CreateTime: 2026-03-13
 */
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GuideAddDTO {
    /** 关联政策id（必填） */
    @NotNull(message = "关联政策id不能为空")
    private Integer policyId;
    /** 申报流程步骤（JSON，必填） */
    @NotBlank(message = "申报步骤不能为空")
    private String steps;
    /** 易错点提醒（必填） */
    @NotBlank(message = "易错点提醒不能为空")
    private String materials;
    /** 材料清单（必填） */
    @NotBlank(message = "材料清单不能为空")
    private String tips;
}
