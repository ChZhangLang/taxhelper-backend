package com.zihao.taxhelperai.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordQueryRequest;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 计税记录服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface TaxRecordService extends IService<TaxRecord> {

    /**
     * 校验数据
     *
     * @param taxRecord
     * @param add 对创建的数据进行校验
     */
    void validTaxRecord(TaxRecord taxRecord, boolean add);

    /**
     * 获取查询条件
     *
     * @param taxRecordQueryRequest
     * @return
     */
    QueryWrapper<TaxRecord> getQueryWrapper(TaxRecordQueryRequest taxRecordQueryRequest);
    
    /**
     * 获取计税记录封装
     *
     * @param taxRecord
     * @param request
     * @return
     */
    TaxRecordVO getTaxRecordVO(TaxRecord taxRecord, HttpServletRequest request);

    /**
     * 分页获取计税记录封装
     *
     * @param taxRecordPage
     * @param request
     * @return
     */
    Page<TaxRecordVO> getTaxRecordVOPage(Page<TaxRecord> taxRecordPage, HttpServletRequest request);
}
