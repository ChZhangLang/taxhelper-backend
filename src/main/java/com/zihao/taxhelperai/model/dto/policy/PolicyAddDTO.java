package com.zihao.taxhelperai.model.dto.policy;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author: 张梓豪
 * @CreateTime: 2026-03-13
 */

@Data
public class PolicyAddDTO {
    /** 政策标题（必填） */
    @NotBlank(message = "政策标题不能为空")
    @Size(max = 50, message = "标题长度不能超过50")
    private String title;
    /** 政策内容（必填） */
    @NotBlank(message = "政策内容不能为空")
    private String content;
    /** 政策类型（必填） */
    @NotNull(message = "政策类型不能为空")
    private Byte type;
    /** 创建人（必填，管理员id） */
    @NotNull(message = "创建人不能为空")
    private Integer createUser;
}
