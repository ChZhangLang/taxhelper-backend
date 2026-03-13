package com.zihao.taxhelperai.model.vo;

/**
 * @Author: 张梓豪
 * @CreateTime: 2026-03-13
 */
import lombok.Data;

@Data
public class GuideVO {
    /** 指引id */
    private Integer id;
    /** 申报流程步骤（JSON） */
    private String steps;
    /** 易错点提醒 */
    private String tips;
    /** 材料清单 */
    private String materials;
}
