package com.zihao.taxhelperai.model.dto.specialDeduct;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class HouseRentDTO {

    private String rentAddress;

    private Integer cityLevel;

    private Integer hasHouseInCity;

    private BigDecimal monthlyRent;

    private Date startDate;

    private Date endDate;
}