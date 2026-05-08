package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName(value = "deduct_elder_support")
@Data
public class DeductElderSupport implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("deduct_id")
    private Long deductId;

    @TableField("elder_name")
    private String elderName;

    @TableField("elder_id_card")
    private String elderIdCard;

    @TableField("elder_age")
    private Integer elderAge;

    @TableField("is_only_child")
    private Integer isOnlyChild;

    @TableField("shared_count")
    private Integer sharedCount;

    @TableField("shared_ratio")
    private BigDecimal sharedRatio;

    @TableField("monthly_amount")
    private BigDecimal monthlyAmount;

    private static final long serialVersionUID = 1L;
}