package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName(value = "deduct_house_rent")
@Data
public class DeductHouseRent implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("deduct_id")
    private Long deductId;

    @TableField("rent_address")
    private String rentAddress;

    @TableField("city_level")
    private Integer cityLevel;

    @TableField("has_house_in_city")
    private Integer hasHouseInCity;

    @TableField("monthly_rent")
    private BigDecimal monthlyRent;

    @TableField("monthly_amount")
    private BigDecimal monthlyAmount;

    private static final long serialVersionUID = 1L;
}