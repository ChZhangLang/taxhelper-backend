package com.zihao.taxhelperai.model.vo;

import com.zihao.taxhelperai.model.entity.TaxSpecialDeduct;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TaxSpecialDeductVO {

    private Long id;

    private Long userId;

    private Integer deductType;

    private String deductTypeName;

    private BigDecimal monthlyAmount;

    private BigDecimal amount;

    private Date startDate;

    private Date endDate;

    private Integer status;

    private Date createTime;

    public static TaxSpecialDeductVO objToVo(TaxSpecialDeduct obj) {
        if (obj == null) {
            return null;
        }
        TaxSpecialDeductVO vo = new TaxSpecialDeductVO();
        vo.setId(obj.getId());
        vo.setUserId(obj.getUserId());
        vo.setDeductType(obj.getDeductType());
        vo.setDeductTypeName(getTypeName(obj.getDeductType()));
        vo.setStartDate(obj.getStartDate());
        vo.setEndDate(obj.getEndDate());
        vo.setStatus(obj.getStatus());
        vo.setCreateTime(obj.getCreateTime());
        return vo;
    }

    private static String getTypeName(Integer type) {
        switch (type) {
            case 1: return "子女教育";
            case 2: return "继续教育";
            case 3: return "大病医疗";
            case 4: return "住房贷款利息";
            case 5: return "住房租金";
            case 6: return "赡养老人";
            default: return "未知";
        }
    }
}