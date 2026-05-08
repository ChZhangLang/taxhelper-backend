package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName(value = "deduct_serious_illness")
@Data
public class DeductSeriousIllness implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("deduct_id")
    private Long deductId;

    @TableField("patient_name")
    private String patientName;

    @TableField("patient_relation")
    private Integer patientRelation;

    @TableField("total_medical_expense")
    private BigDecimal totalMedicalExpense;

    @TableField("insurance_reimburse")
    private BigDecimal insuranceReimburse;

    @TableField("self_pay_expense")
    private BigDecimal selfPayExpense;

    @TableField("deductible_amount")
    private BigDecimal deductibleAmount;

    private Integer year;

    private static final long serialVersionUID = 1L;
}