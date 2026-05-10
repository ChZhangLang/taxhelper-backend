package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "tax_special_deduct")
@Data
public class TaxSpecialDeduct implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("deduct_type")
    private Integer deductType;

    @TableField("start_date")
    private Date startDate;

    @TableField("end_date")
    private Date endDate;

    private Integer status;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private java.math.BigDecimal childEducationAmount;

    @TableField(exist = false)
    private java.math.BigDecimal continueEducationAmount;

    @TableField(exist = false)
    private java.math.BigDecimal houseLoanAmount;

    @TableField(exist = false)
    private java.math.BigDecimal houseRentAmount;

    @TableField(exist = false)
    private java.math.BigDecimal elderSupportAmount;

    @TableField(exist = false)
    private java.math.BigDecimal seriousIllnessAmount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}