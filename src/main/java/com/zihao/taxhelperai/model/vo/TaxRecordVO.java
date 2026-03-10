package com.zihao.taxhelperai.model.vo;

import cn.hutool.json.JSONUtil;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 计税记录视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class TaxRecordVO implements Serializable {

    /**
     * 记录ID
     */
    private Long id;

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
     * 计算类型名称（方便前端展示）
     */
    private String calcTypeName;

    /**
     * 计算时间
     */
    private Date calcTime;

    /**
     * 封装类转对象
     *
     * @param taxRecordVO
     * @return
     */
    public static TaxRecord voToObj(TaxRecordVO taxRecordVO) {
        if (taxRecordVO == null) {
            return null;
        }
        TaxRecord taxRecord = new TaxRecord();
        BeanUtils.copyProperties(taxRecordVO, taxRecord);
//        List<String> tagList = taxRecordVO.getTagList();
        // todo 没有tag
//        taxRecord.setTags(JSONUtil.toJsonStr(tagList));
        return taxRecord;
    }

    /**
     * 对象转封装类
     *
     * @param taxRecord
     * @return
     */
    public static TaxRecordVO objToVo(TaxRecord taxRecord) {
        if (taxRecord == null) {
            return null;
        }
        TaxRecordVO taxRecordVO = new TaxRecordVO();
        BeanUtils.copyProperties(taxRecord, taxRecordVO);
        // todo 没有tag
//        taxRecordVO.setTagList(JSONUtil.toList(taxRecord.getTags(), String.class));
        return taxRecordVO;
    }
}
