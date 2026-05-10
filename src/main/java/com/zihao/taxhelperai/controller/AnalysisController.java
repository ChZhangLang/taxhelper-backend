package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.annotation.AuthCheck;
import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.constant.UserConstant;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.AnalysisVO;
import com.zihao.taxhelperai.service.AnalysisService;
import com.zihao.taxhelperai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/annual")
    public BaseResponse<AnalysisVO.AnnualSummary> getUserAnnualSummary(
            @RequestParam(defaultValue = "2026") Integer year,
            HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        AnalysisVO.AnnualSummary summary = analysisService.getUserAnnualSummary(user.getId(), year);
        return ResultUtils.success(summary);
    }

    @GetMapping("/user/monthly-trend")
    public BaseResponse<List<AnalysisVO.MonthlyTrend>> getUserMonthlyTrend(
            @RequestParam(defaultValue = "2026") Integer year,
            HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        List<AnalysisVO.MonthlyTrend> trends = analysisService.getUserMonthlyTrend(user.getId(), year);
        return ResultUtils.success(trends);
    }

    @GetMapping("/user/deduct-ratio")
    public BaseResponse<List<AnalysisVO.DeductRatio>> getUserDeductRatio(
            @RequestParam(defaultValue = "2026") Integer year,
            HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        List<AnalysisVO.DeductRatio> ratios = analysisService.getUserDeductRatio(user.getId(), year);
        return ResultUtils.success(ratios);
    }

    @GetMapping("/user/report")
    public BaseResponse<AnalysisVO.TaxAnalysisReport> generateTaxReport(
            @RequestParam(defaultValue = "2026") Integer year,
            HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        AnalysisVO.TaxAnalysisReport report = analysisService.generateTaxReport(user.getId(), year);
        return ResultUtils.success(report);
    }

    @GetMapping("/platform/stats")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AnalysisVO.PlatformStats> getPlatformStats() {
        AnalysisVO.PlatformStats stats = analysisService.getPlatformStats();
        return ResultUtils.success(stats);
    }

    @GetMapping("/platform/income-distribution")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<AnalysisVO.IncomeDistribution>> getIncomeDistribution() {
        List<AnalysisVO.IncomeDistribution> distribution = analysisService.getIncomeDistribution();
        return ResultUtils.success(distribution);
    }

    @GetMapping("/platform/city-tax")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<AnalysisVO.CityTaxStats>> getCityTaxStats() {
        List<AnalysisVO.CityTaxStats> stats = analysisService.getCityTaxStats();
        return ResultUtils.success(stats);
    }
}
