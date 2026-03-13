package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 税务政策表
 * @TableName policy
 */
@TableName(value ="policy")
@Data
public class Policy implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 政策标题
     */
    private String title;

    /**
     * 政策内容
     */
    private String content;

    /**
     * 政策类型 1-基础政策 2-扣除标准 3-申报流程
     */
    private Integer type;

    /**
     * 创建人(管理员id)
     */
    private Integer createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}