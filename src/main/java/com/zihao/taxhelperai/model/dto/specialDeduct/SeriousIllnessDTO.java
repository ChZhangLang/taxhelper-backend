package com.zihao.taxhelperai.model.dto.specialDeduct;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeriousIllnessDTO {

    private String patientName;

    private Integer patientRelation;

    private BigDecimal totalMedicalExpense;

    private BigDecimal insuranceReimburse;

    private BigDecimal selfPayExpense;

    private Integer year;

    private Date startDate;

    private Date endDate;
}