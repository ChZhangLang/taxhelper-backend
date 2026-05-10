package com.zihao.taxhelperai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxCalculateRequest;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordQueryRequest;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.vo.TaxCalculateVO;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;
import com.zihao.taxhelperai.model.vo.TaxStatsVO;
import com.zihao.taxhelperai.model.vo.TaxSettlementVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 计税记录服务接口
 *
 * @author 你的名字
 */
public interface TaxRecordService extends IService<TaxRecord> {

    /**
     * 计算个税并保存记录（原有方法，保留兼容）
     *
     * @param taxCalculateRequest 计算请求
     * @param userId 登录用户ID
     * @return 计算结果VO
     */
    TaxCalculateVO calculateAndSaveTax(TaxCalculateRequest taxCalculateRequest, Long userId);

    /**
     * 使用累计预扣法计算月度税额
     *
     * @param taxCalculateRequest 计算请求
     * @param userId 登录用户ID
     * @return 计算结果VO
     */
    TaxCalculateVO calculateMonthlyWithCumulative(TaxCalculateRequest taxCalculateRequest, Long userId);

    /**
     * 计算年度汇算清缴
     *
     * @param userId 用户ID
     * @param year 年度
     * @return 汇算结果VO
     */
    TaxSettlementVO calculateAnnualSettlement(Long userId, Integer year);

    /**
     * 获取用户当年累计数据
     *
     * @param userId 用户ID
     * @param year 年度
     * @return 累计数据Map
     */
    Map<String, BigDecimal> getCumulativeData(Long userId, Integer year);

    /**
     * 分页查询计税记录VO
     *
     * @param taxRecordQueryRequest 查询条件
     * @return 分页VO
     */
    Page<TaxRecordVO> listTaxRecordVOByPage(TaxRecordQueryRequest taxRecordQueryRequest);

    /**
     * 计算月薪个税（核心算法）
     *
     * @param income 收入金额
     * @param insurance 五险一金
     * @param deduct 专项附加扣除
     * @return 应缴税额
     */
    BigDecimal calculateMonthlyTax(BigDecimal income, BigDecimal insurance, BigDecimal deduct);

    /**
     * 计算年度汇算个税（核心算法）
     *
     * @param income 年度总收入
     * @param insurance 年度五险一金
     * @param deduct 年度专项附加扣除
     * @return 应缴税额
     */
    BigDecimal calculateAnnualTax(BigDecimal income, BigDecimal insurance, BigDecimal deduct);

    /**
     * 获取税收统计分析数据
     *
     * @return 统计VO
     */
    TaxStatsVO getTaxStats();

    /**
     * 获取用户指定年度的计税记录列表
     *
     * @param userId 用户ID
     * @param year 年度
     * @return 计税记录列表
     */
    java.util.List<com.zihao.taxhelperai.model.entity.TaxRecord> getRecordsByUserIdAndYear(Long userId, Integer year);
}