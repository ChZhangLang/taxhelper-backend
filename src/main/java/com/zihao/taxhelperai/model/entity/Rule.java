package com.zihao.taxhelperai.model.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 计税规则实体
 */
@Data
public class Rule {
    /**
     * 扣除项目名称
     */
    private String name;

    /**
     * 扣除金额/数值，兼容小数计税
     */
    private BigDecimal amount;

    /**
     * 周期单位：per_month 按月 / per_year 按年
     */
    private String unit;
}