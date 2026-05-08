package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName(value = "deduct_house_loan")
@Data
public class DeductHouseLoan implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("deduct_id")
    private Long deductId;

    @TableField("house_address")
    private String houseAddress;

    @TableField("is_first_house")
    private Integer isFirstHouse;

    @TableField("loan_bank")
    private String loanBank;

    @TableField("loan_start_date")
    private Date loanStartDate;

    @TableField("total_months")
    private Integer totalMonths;

    @TableField("used_months")
    private Integer usedMonths;

    @TableField("monthly_amount")
    private BigDecimal monthlyAmount;

    private static final long serialVersionUID = 1L;
}