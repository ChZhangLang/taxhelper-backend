package com.zihao.taxhelperai.mapper;

import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.vo.TaxStatsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
* @author MI
* @description 针对表【tax_record(计税记录表)】的数据库操作Mapper
* @createDate 2026-03-08 16:18:07
* @Entity com.zihao.taxhelperai.model.entity.TaxRecord
*/
public interface TaxRecordMapper extends BaseMapper<TaxRecord> {

    /**
     * 查询总记录数
     */
    Long selectTotalCount();

    /**
     * 查询当月总税额
     */
    BigDecimal selectCurrentMonthTax();

    /**
     * 查询当年总税额
     */
    BigDecimal selectCurrentYearTax();

    /**
     * 按用户维度汇总税收
     */
    List<TaxStatsVO.UserTaxSummary> selectUserTaxSummary();

    /**
     * 按月份维度汇总税收
     */
    List<TaxStatsVO.MonthTaxSummary> selectMonthTaxSummary();

    /**
     * 获取用户指定年度的累计收入
     */
    BigDecimal getCumulativeIncome(@Param("userId") Long userId, @Param("taxYear") Integer taxYear);

    /**
     * 获取用户指定年度的累计五险一金
     */
    BigDecimal getCumulativeInsurance(@Param("userId") Long userId, @Param("taxYear") Integer taxYear);

    /**
     * 获取用户指定年度的累计专项附加扣除
     */
    BigDecimal getCumulativeDeduct(@Param("userId") Long userId, @Param("taxYear") Integer taxYear);

    /**
     * 获取用户指定年度的累计已预扣税
     */
    BigDecimal getCumulativeTax(@Param("userId") Long userId, @Param("taxYear") Integer taxYear);

    /**
     * 获取用户指定年度所有月度记录
     */
    List<TaxRecord> selectYearRecords(@Param("userId") Long userId, @Param("taxYear") Integer taxYear);

    /**
     * 统计收入在指定范围内的记录数
     */
    long countByIncomeRange(@Param("minIncome") BigDecimal minIncome, @Param("maxIncome") BigDecimal maxIncome);

    /**
     * 按城市维度汇总税收
     */
    List<TaxStatsVO.CityTaxSummary> selectCityTaxSummary();
}
