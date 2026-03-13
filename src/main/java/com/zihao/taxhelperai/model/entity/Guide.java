package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 申报指引表
 * @TableName guide
 */
@TableName(value ="guide")
@Data
public class Guide implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 关联政策id
     */
    private Integer policyId;

    /**
     * 申报流程步骤(JSON)
     */
    private String steps;

    /**
     * 易错点提醒
     */
    private String tips;

    /**
     * 材料清单
     */
    private String materials;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}