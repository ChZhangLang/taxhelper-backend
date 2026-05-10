package com.zihao.taxhelperai.service.impl;

import com.zihao.taxhelperai.ai.AiService;
import com.zihao.taxhelperai.model.entity.TaxMessage;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.entity.TaxSpecialDeduct;
import com.zihao.taxhelperai.service.MessageGeneratorService;
import com.zihao.taxhelperai.service.TaxMessageService;
import com.zihao.taxhelperai.service.TaxRecordService;
import com.zihao.taxhelperai.service.TaxSpecialDeductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息生成服务实现类
 */
@Service
@Slf4j
public class MessageGeneratorServiceImpl implements MessageGeneratorService {

    @Autowired
    private TaxMessageService taxMessageService;

    @Autowired
    private TaxRecordService taxRecordService;

    @Autowired
    private TaxSpecialDeductService taxSpecialDeductService;

    @Autowired
    private AiService aiService;

    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("1.00");

    @Override
    public List<TaxMessage> generateAnnualTaxMessages(Long userId, BigDecimal totalIncome,
                                                       BigDecimal totalTax, BigDecimal taxPayable) {
        List<TaxMessage> messages = new ArrayList<>();

        if (taxPayable == null) {
            taxPayable = BigDecimal.ZERO;
        }

        // 补税提醒
        if (taxPayable.compareTo(TAX_THRESHOLD) > 0) {
            String content = String.format("您本年度预计需补税 %.2f 元，请及时完成申报缴纳。", taxPayable);
            content = optimizeWithAI(content);
            TaxMessage message = taxMessageService.createTaxPayMessage(userId, content);
            if (message != null) {
                messages.add(message);
            }
        }
        // 退税提醒
        else if (taxPayable.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal refundAmount = taxPayable.abs();
            String content = String.format("系统检测您可申请退税 %.2f 元，请在申报期间完成退税申请。", refundAmount);
            content = optimizeWithAI(content);
            TaxMessage message = taxMessageService.createTaxRefundMessage(userId, content);
            if (message != null) {
                messages.add(message);
            }
        }
        // 无需补退税
        else {
            String content = String.format("您本年度应纳税额已结清，无需补税或退税。总收入：%.2f 元，已缴税额：%.2f 元。",
                    totalIncome, totalTax);
            content = optimizeWithAI(content);
            TaxMessage message = taxMessageService.createSystemMessage(userId, "年度税务结果通知", content);
            if (message != null) {
                messages.add(message);
            }
        }

        log.info("为用户 {} 生成年度税务消息 {} 条", userId, messages.size());
        return messages;
    }

    @Override
    public List<TaxMessage> generateMonthlyAnalysisMessages(Long userId, BigDecimal income,
                                                            BigDecimal lastMonthIncome,
                                                            BigDecimal tax, BigDecimal lastMonthTax) {
        List<TaxMessage> messages = new ArrayList<>();

        // 收入波动检测（波动超过30%）
        if (lastMonthIncome != null && lastMonthIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal incomeChange = income.subtract(lastMonthIncome)
                    .divide(lastMonthIncome, 4, RoundingMode.HALF_UP).abs();
            
            if (incomeChange.compareTo(new BigDecimal("0.3")) > 0) {
                String changeType = income.compareTo(lastMonthIncome) > 0 ? "增加" : "减少";
                String content = String.format("您本月收入较上月%s超过30%%，请注意核查数据是否准确。本月收入：%.2f 元，上月收入：%.2f 元。",
                        changeType, income, lastMonthIncome);
                content = optimizeWithAI(content);
                TaxMessage message = taxMessageService.createRiskMessage(userId, content);
                if (message != null) {
                    messages.add(message);
                }
            }
        }

        // 税负变化检测
        if (lastMonthTax != null && lastMonthTax.compareTo(BigDecimal.ZERO) > 0 && tax.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxRate = tax.divide(income, 4, RoundingMode.HALF_UP);
            BigDecimal lastMonthTaxRate = lastMonthTax.divide(lastMonthIncome, 4, RoundingMode.HALF_UP);
            BigDecimal rateChange = taxRate.subtract(lastMonthTaxRate).abs();

            if (rateChange.compareTo(new BigDecimal("0.1")) > 0) {
                String changeType = taxRate.compareTo(lastMonthTaxRate) > 0 ? "增加" : "减少";
                String content = String.format("您本月税负率较上月%s超过10个百分点，请关注税率变化原因。本月税负率：%.2f%%，上月税负率：%.2f%%。",
                        changeType, taxRate.multiply(new BigDecimal("100")), lastMonthTaxRate.multiply(new BigDecimal("100")));
                content = optimizeWithAI(content);
                TaxMessage message = taxMessageService.createRiskMessage(userId, content);
                if (message != null) {
                    messages.add(message);
                }
            }
        }

        log.info("为用户 {} 生成月度分析消息 {} 条", userId, messages.size());
        return messages;
    }

    @Override
    public List<TaxMessage> generateDeductAnalysisMessages(Long userId, Integer taxYear) {
        List<TaxMessage> messages = new ArrayList<>();

        // 获取用户专项附加扣除信息
        TaxSpecialDeduct deduct = taxSpecialDeductService.getByUserIdAndYear(userId, taxYear);
        if (deduct == null) {
            deduct = new TaxSpecialDeduct();
        }

        StringBuilder suggestions = new StringBuilder();
        boolean hasSuggestions = false;

        // 检查各项扣除是否填写
        if (deduct.getChildEducationAmount() == null || deduct.getChildEducationAmount().compareTo(BigDecimal.ZERO) == 0) {
            suggestions.append("子女教育专项附加扣除未填写；");
            hasSuggestions = true;
        }
        if (deduct.getContinueEducationAmount() == null || deduct.getContinueEducationAmount().compareTo(BigDecimal.ZERO) == 0) {
            suggestions.append("继续教育专项附加扣除未填写；");
            hasSuggestions = true;
        }
        if (deduct.getHouseLoanAmount() == null || deduct.getHouseLoanAmount().compareTo(BigDecimal.ZERO) == 0) {
            suggestions.append("住房贷款利息专项附加扣除未填写；");
            hasSuggestions = true;
        }
        if (deduct.getHouseRentAmount() == null || deduct.getHouseRentAmount().compareTo(BigDecimal.ZERO) == 0) {
            suggestions.append("住房租金专项附加扣除未填写；");
            hasSuggestions = true;
        }
        if (deduct.getElderSupportAmount() == null || deduct.getElderSupportAmount().compareTo(BigDecimal.ZERO) == 0) {
            suggestions.append("赡养老人专项附加扣除未填写；");
            hasSuggestions = true;
        }
        if (deduct.getSeriousIllnessAmount() == null || deduct.getSeriousIllnessAmount().compareTo(BigDecimal.ZERO) == 0) {
            suggestions.append("大病医疗专项附加扣除未填写；");
            hasSuggestions = true;
        }

        if (hasSuggestions) {
            String content = "AI分析发现您可能遗漏以下专项附加扣除项目：" + suggestions.toString() +
                    "建议您检查是否符合相关扣除条件，及时填写可享受税收优惠。";
            content = optimizeWithAI(content);
            TaxMessage message = taxMessageService.createAiAdviceMessage(userId, content);
            if (message != null) {
                messages.add(message);
            }
        }

        log.info("为用户 {} 生成专项附加扣除分析消息 {} 条", userId, messages.size());
        return messages;
    }

    @Override
    public List<TaxMessage> generateRiskWarningMessages(Long userId, Integer taxYear) {
        List<TaxMessage> messages = new ArrayList<>();

        // 获取用户税务记录
        List<TaxRecord> records = taxRecordService.getRecordsByUserIdAndYear(userId, taxYear);

        // 检查是否有长时间未申报
        if (CollectionUtils.isEmpty(records)) {
            String content = "检测到您在 " + taxYear + " 年度尚未有申报记录，请及时完成税务申报。";
            content = optimizeWithAI(content);
            TaxMessage message = taxMessageService.createRiskMessage(userId, content);
            if (message != null) {
                messages.add(message);
            }
            return messages;
        }

        // 检查收入异常波动
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal maxIncome = BigDecimal.ZERO;
        BigDecimal minIncome = null;

        for (TaxRecord record : records) {
            BigDecimal income = record.getIncome();
            totalIncome = totalIncome.add(income);

            if (income.compareTo(maxIncome) > 0) {
                maxIncome = income;
            }
            if (minIncome == null || income.compareTo(minIncome) < 0) {
                minIncome = income;
            }
        }

        // 如果收入波动超过200%
        if (minIncome != null && minIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal volatility = maxIncome.subtract(minIncome).divide(minIncome, 4, RoundingMode.HALF_UP);
            if (volatility.compareTo(new BigDecimal("2")) > 0) {
                String content = String.format("检测到您的收入存在异常波动，最高收入与最低收入差距超过200%%。建议核查收入数据是否准确。", volatility);
                content = optimizeWithAI(content);
                TaxMessage message = taxMessageService.createRiskMessage(userId, content);
                if (message != null) {
                    messages.add(message);
                }
            }
        }

        log.info("为用户 {} 生成风险预警消息 {} 条", userId, messages.size());
        return messages;
    }

    @Override
    public String optimizeWithAI(String content) {
        try {
            String prompt = String.format("请将以下税务消息优化为友好、专业的通知文案，只需返回一个版本，不要包含多个版本选项和优化说明。消息内容：%s", content);
            String result = aiService.call(prompt);
            if (result != null && !result.isEmpty()) {
                return result.trim();
            }
        } catch (Exception e) {
            log.warn("AI优化消息失败，使用原始消息: {}", e.getMessage());
        }
        return content;
    }
}