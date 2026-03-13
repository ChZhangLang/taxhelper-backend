package com.zihao.taxhelperai.model.vo;

/**
 * @Author: 张梓豪
 * @CreateTime: 2026-03-13
 */
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PolicyVO {
    /** 政策id */
    private Integer id;
    /** 政策标题 */
    private String title;
    /** 政策内容 */
    private String content;
    /** 政策类型（转中文，前端友好） */
    private String typeDesc;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 关联的申报指引（仅政策类型为3时返回） */
    private List<GuideVO> guideList;

    // 政策类型转中文（工具方法）
    public void setTypeDesc(Integer type) {
        switch (type) {
            case 1: this.typeDesc = "基础政策"; break;
            case 2: this.typeDesc = "扣除标准"; break;
            case 3: this.typeDesc = "申报流程"; break;
            default: this.typeDesc = "未知类型";
        }
    }
}
