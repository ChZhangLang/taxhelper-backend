package com.zihao.taxhelperai.service;

import com.zihao.taxhelperai.model.vo.AnalysisVO;

import java.util.List;

public interface AnalysisService {

    AnalysisVO.AnnualSummary getUserAnnualSummary(Long userId, Integer taxYear);

    List<AnalysisVO.MonthlyTrend> getUserMonthlyTrend(Long userId, Integer taxYear);

    List<AnalysisVO.DeductRatio> getUserDeductRatio(Long userId, Integer taxYear);

    AnalysisVO.TaxAnalysisReport generateTaxReport(Long userId, Integer taxYear);

    AnalysisVO.PlatformStats getPlatformStats();

    List<AnalysisVO.IncomeDistribution> getIncomeDistribution();

    List<AnalysisVO.CityTaxStats> getCityTaxStats();
}
