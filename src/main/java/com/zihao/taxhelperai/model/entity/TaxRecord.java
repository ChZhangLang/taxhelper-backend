package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 计税记录表
 * @TableName tax_record
 */
@TableName(value ="tax_record")
@Data
public class TaxRecord implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联用户id
     */
    private Long userId;

    /**
     * 收入金额
     */
    private BigDecimal income;

    /**
     * 五险一金
     */
    private BigDecimal insurance;

    /**
     * 专项附加扣除
     */
    private BigDecimal deduct;

    /**
     * 应缴税额
     */
    private BigDecimal taxAmount;

    /**
     * 计算类型 1-月薪 2-年度汇算
     */
    private Integer calcType;

    /**
     * 计算时间
     */
    private Date calcTime;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}