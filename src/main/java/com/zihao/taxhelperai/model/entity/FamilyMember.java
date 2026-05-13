package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 家属信息表
 * @TableName family_member
 */
@TableName(value ="family_member")
@Data
public class FamilyMember implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联用户id
     */
    private Long userId;

    /**
     * 与用户的关系（如：配偶、子女、父母等）
     */
    private String relationship;

    /**
     * 证件类型
     */
    private String idCardType;

    /**
     * 证件号码
     */
    private String idCard;

    /**
     * 家属姓名
     */
    private String name;

    /**
     * 国籍(地区)
     */
    private String nationality;

    /**
     * 出生日期（根据证件号自动填入）
     */
    private Date birthDate;

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
    @TableLogic(value = "0", delval = "1")
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}