package com.zihao.taxhelperai.model.dto.specialDeduct;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ChildEducationDTO {

    private String childName;

    private String childIdCard;

    private String educationStage;

    private String schoolName;

    private Integer childCount;

    private Integer isShared;

    private BigDecimal sharedRatio;

    private Date startDate;

    private Date endDate;
}