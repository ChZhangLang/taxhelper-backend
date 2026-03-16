package com.zihao.taxhelperai.model.vo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 专项附加扣除VO
 */
@Data
public class SpecialDeductionVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 扣除类型：1-子女教育 2-继续教育 3-大病医疗 4-住房贷款 5-住房租金 6-赡养老人
     */
    private Integer deductionType;

    /**
     * 扣除类型名称
     */
    private String deductionTypeName;

    /**
     * 扣除金额
     */
    private BigDecimal amount;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 扣除状态：0-未生效 1-生效中 2-已过期
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 将实体对象转换为VO
     */
    public static SpecialDeductionVO objToVo(Object obj) {
        if (obj == null) {
            return null;
        }
        SpecialDeductionVO vo = new SpecialDeductionVO();
        try {
            // 使用反射复制属性
            java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                java.lang.reflect.Field voField = vo.getClass().getDeclaredField(field.getName());
                voField.setAccessible(true);
                voField.set(vo, value);
            }
            // 设置类型名称
            if (vo.getDeductionType() != null) {
                vo.setDeductionTypeName(getDeductionTypeName(vo.getDeductionType()));
            }
            // 设置状态名称
            if (vo.getStatus() != null) {
                vo.setStatusName(getStatusName(vo.getStatus()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * 获取扣除类型名称
     */
    private static String getDeductionTypeName(Integer deductionType) {
        switch (deductionType) {
            case 1:
                return "子女教育";
            case 2:
                return "继续教育";
            case 3:
                return "大病医疗";
            case 4:
                return "住房贷款";
            case 5:
                return "住房租金";
            case 6:
                return "赡养老人";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态名称
     */
    private static String getStatusName(Integer status) {
        switch (status) {
            case 0:
                return "未生效";
            case 1:
                return "生效中";
            case 2:
                return "已过期";
            default:
                return "未知";
        }
    }
}
