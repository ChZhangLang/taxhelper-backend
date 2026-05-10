package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.entity.TaxMessage;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.service.MessageGeneratorService;
import com.zihao.taxhelperai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息生成控制器
 */
@RestController
@RequestMapping("/message/generator")
@Slf4j
public class MessageGeneratorController {

    @Autowired
    private MessageGeneratorService messageGeneratorService;

    @Autowired
    private UserService userService;

    /**
     * 触发年度税务消息生成
     */
    @PostMapping("/annual")
    public BaseResponse<Map<String, Object>> triggerAnnualMessages(
            @RequestParam BigDecimal totalIncome,
            @RequestParam BigDecimal totalTax,
            @RequestParam BigDecimal taxPayable,
            HttpServletRequest request) {
        
        User user = userService.getLoginUser(request);
        List<TaxMessage> messages = messageGeneratorService.generateAnnualTaxMessages(
                user.getId(), totalIncome, totalTax, taxPayable);

        Map<String, Object> result = new HashMap<>();
        result.put("messageCount", messages.size());
        result.put("messageIds", messages.stream().map(TaxMessage::getId).collect(Collectors.toList()));
        
        log.info("触发年度消息生成成功，用户: {}, 生成消息数: {}", user.getId(), messages.size());
        return ResultUtils.success(result);
    }

    /**
     * 触发月度分析消息生成
     */
    @PostMapping("/monthly")
    public BaseResponse<Map<String, Object>> triggerMonthlyMessages(
            @RequestParam BigDecimal income,
            @RequestParam(required = false) BigDecimal lastMonthIncome,
            @RequestParam BigDecimal tax,
            @RequestParam(required = false) BigDecimal lastMonthTax,
            HttpServletRequest request) {
        
        User user = userService.getLoginUser(request);
        List<TaxMessage> messages = messageGeneratorService.generateMonthlyAnalysisMessages(
                user.getId(), income, lastMonthIncome, tax, lastMonthTax);

        Map<String, Object> result = new HashMap<>();
        result.put("messageCount", messages.size());
        result.put("messageIds", messages.stream().map(TaxMessage::getId).collect(Collectors.toList()));
        
        log.info("触发月度消息生成成功，用户: {}, 生成消息数: {}", user.getId(), messages.size());
        return ResultUtils.success(result);
    }

    /**
     * 触发专项附加扣除分析消息生成
     */
    @PostMapping("/deduct")
    public BaseResponse<Map<String, Object>> triggerDeductMessages(
            @RequestParam(defaultValue = "2026") Integer taxYear,
            HttpServletRequest request) {
        
        User user = userService.getLoginUser(request);
        List<TaxMessage> messages = messageGeneratorService.generateDeductAnalysisMessages(user.getId(), taxYear);

        Map<String, Object> result = new HashMap<>();
        result.put("messageCount", messages.size());
        result.put("messageIds", messages.stream().map(TaxMessage::getId).collect(Collectors.toList()));
        
        log.info("触发专项附加扣除分析消息生成成功，用户: {}, 生成消息数: {}", user.getId(), messages.size());
        return ResultUtils.success(result);
    }

    /**
     * 触发风险预警消息生成
     */
    @PostMapping("/risk")
    public BaseResponse<Map<String, Object>> triggerRiskMessages(
            @RequestParam(defaultValue = "2026") Integer taxYear,
            HttpServletRequest request) {
        
        User user = userService.getLoginUser(request);
        List<TaxMessage> messages = messageGeneratorService.generateRiskWarningMessages(user.getId(), taxYear);

        Map<String, Object> result = new HashMap<>();
        result.put("messageCount", messages.size());
        result.put("messageIds", messages.stream().map(TaxMessage::getId).collect(Collectors.toList()));
        
        log.info("触发风险预警消息生成成功，用户: {}, 生成消息数: {}", user.getId(), messages.size());
        return ResultUtils.success(result);
    }

    /**
     * 触发完整的消息分析（所有类型）
     */
    @PostMapping("/full-analysis")
    public BaseResponse<Map<String, Object>> triggerFullAnalysis(
            @RequestParam(defaultValue = "2026") Integer taxYear,
            @RequestParam(required = false) BigDecimal totalIncome,
            @RequestParam(required = false) BigDecimal totalTax,
            @RequestParam(required = false) BigDecimal taxPayable,
            HttpServletRequest request) {
        
        User user = userService.getLoginUser(request);
        
        // 生成各类消息
        List<TaxMessage> riskMessages = messageGeneratorService.generateRiskWarningMessages(user.getId(), taxYear);
        List<TaxMessage> deductMessages = messageGeneratorService.generateDeductAnalysisMessages(user.getId(), taxYear);
        
        int totalCount = riskMessages.size() + deductMessages.size();
        
        // 如果提供了税务计算数据，也生成税务结果消息
        if (totalIncome != null && totalTax != null) {
            List<TaxMessage> annualMessages = messageGeneratorService.generateAnnualTaxMessages(
                    user.getId(), totalIncome, totalTax, taxPayable);
            totalCount += annualMessages.size();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalMessageCount", totalCount);
        result.put("riskMessages", riskMessages.size());
        result.put("deductMessages", deductMessages.size());
        
        log.info("触发完整消息分析成功，用户: {}, 生成消息数: {}", user.getId(), totalCount);
        return ResultUtils.success(result);
    }
}