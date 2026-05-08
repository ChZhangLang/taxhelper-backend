package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName(value = "deduct_child_education")
@Data
public class DeductChildEducation implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("deduct_id")
    private Long deductId;

    @TableField("child_name")
    private String childName;

    @TableField("child_id_card")
    private String childIdCard;

    @TableField("education_stage")
    private String educationStage;

    @TableField("school_name")
    private String schoolName;

    @TableField("is_shared")
    private Integer isShared;

    @TableField("shared_ratio")
    private BigDecimal sharedRatio;

    @TableField("monthly_amount")
    private BigDecimal monthlyAmount;

    private static final long serialVersionUID = 1L;
}