package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 银行卡信息表
 * @TableName bank_card
 */
@TableName(value ="bank_card")
@Data
public class BankCard implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联用户id
     */
    private Long userId;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 所属银行
     */
    private String bankName;

    /**
     * 开户银行所在省份
     */
    private String province;

    /**
     * 银行预留手机号码
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @TableLogic(value = "0", delval = "1")
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}