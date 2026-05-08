package com.zihao.taxhelperai.model.dto.specialDeduct;

import lombok.Data;

import java.util.Date;

@Data
public class HouseLoanDTO {

    private String houseAddress;

    private Integer isFirstHouse;

    private String loanBank;

    private Date loanStartDate;

    private Integer totalMonths;

    private Date startDate;

    private Date endDate;
}