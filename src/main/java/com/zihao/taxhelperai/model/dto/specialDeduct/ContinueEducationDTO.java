package com.zihao.taxhelperai.model.dto.specialDeduct;

import lombok.Data;

import java.util.Date;

@Data
public class ContinueEducationDTO {

    private Integer educationType;

    private String educationName;

    private String institutionName;

    private String certificateNo;

    private Date startDate;

    private Date endDate;
}