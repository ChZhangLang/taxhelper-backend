package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName(value = "deduct_continue_education")
@Data
public class DeductContinueEducation implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("deduct_id")
    private Long deductId;

    @TableField("education_type")
    private Integer educationType;

    @TableField("education_name")
    private String educationName;

    @TableField("institution_name")
    private String institutionName;

    @TableField("certificate_no")
    private String certificateNo;

    @TableField("monthly_amount")
    private BigDecimal monthlyAmount;

    private static final long serialVersionUID = 1L;
}