package com.zihao.taxhelperai.service;

import com.zihao.taxhelperai.model.entity.TaxMessage;

import java.math.BigDecimal;
import java.util.List;

/**
 * 消息生成服务接口
 */
public interface MessageGeneratorService {

    /**
     * 处理年度税务计算结果，生成相关消息
     */
    List<TaxMessage> generateAnnualTaxMessages(Long userId, BigDecimal totalIncome, 
                                                 BigDecimal totalTax, BigDecimal taxPayable);

    /**
     * 处理月度税务分析，生成相关消息
     */
    List<TaxMessage> generateMonthlyAnalysisMessages(Long userId, BigDecimal income, 
                                                      BigDecimal lastMonthIncome, 
                                                      BigDecimal tax, BigDecimal lastMonthTax);

    /**
     * 分析专项附加扣除，生成建议消息
     */
    List<TaxMessage> generateDeductAnalysisMessages(Long userId, Integer taxYear);

    /**
     * 检测风险并生成预警消息
     */
    List<TaxMessage> generateRiskWarningMessages(Long userId, Integer taxYear);

    /**
     * 使用AI优化消息内容
     */
    String optimizeWithAI(String content);
}