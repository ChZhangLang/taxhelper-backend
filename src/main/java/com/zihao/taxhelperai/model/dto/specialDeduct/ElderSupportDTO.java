package com.zihao.taxhelperai.model.dto.specialDeduct;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ElderSupportDTO {

    private String elderName;

    private String elderIdCard;

    private Integer elderAge;

    private Integer isOnlyChild;

    private Integer sharedCount;

    private BigDecimal sharedRatio;

    private Date startDate;

    private Date endDate;
}