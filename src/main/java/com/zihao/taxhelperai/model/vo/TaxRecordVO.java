package com.zihao.taxhelperai.model.vo;

import cn.hutool.json.JSONUtil;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
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
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

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
        List<String> tagList = taxRecordVO.getTagList();
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
