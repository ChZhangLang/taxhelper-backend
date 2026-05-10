package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zihao.taxhelperai.mapper.*;
import com.zihao.taxhelperai.model.entity.*;
import com.zihao.taxhelperai.model.vo.AnalysisVO;
import com.zihao.taxhelperai.model.vo.TaxStatsVO;
import com.zihao.taxhelperai.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private TaxRecordMapper taxRecordMapper;

    @Autowired
    private TaxSpecialDeductMapper taxSpecialDeductMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeductChildEducationMapper childEducationMapper;

    @Autowired
    private DeductContinueEducationMapper continueEducationMapper;

    @Autowired
    private DeductElderSupportMapper elderSupportMapper;

    @Autowired
    private DeductHouseLoanMapper houseLoanMapper;

    @Autowired
    private DeductHouseRentMapper houseRentMapper;

    @Autowired
    private DeductSeriousIllnessMapper seriousIllnessMapper;

    private static final Map<Integer, String> DEDUCT_TYPE_MAP = new HashMap<>();
    static {
        DEDUCT_TYPE_MAP.put(1, "子女教育");
        DEDUCT_TYPE_MAP.put(2, "继续教育");
        DEDUCT_TYPE_MAP.put(3, "大病医疗");
        DEDUCT_TYPE_MAP.put(4, "住房贷款利息");
        DEDUCT_TYPE_MAP.put(5, "住房租金");
        DEDUCT_TYPE_MAP.put(6, "赡养老人");
    }

    @Override
    public AnalysisVO.AnnualSummary getUserAnnualSummary(Long userId, Integer taxYear) {
        AnalysisVO.AnnualSummary summary = new AnalysisVO.AnnualSummary();
        summary.setTaxYear(taxYear);

        QueryWrapper<TaxRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("taxYear", taxYear)
                .eq("isDelete", 0);
        
        List<TaxRecord> records = taxRecordMapper.selectList(queryWrapper);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDeduct = BigDecimal.ZERO;
        
        for (TaxRecord record : records) {
            if (record.getIncome() != null) totalIncome = totalIncome.add(record.getIncome());
            if (record.getTaxAmount() != null) totalTax = totalTax.add(record.getTaxAmount());
            if (record.getDeduct() != null) totalDeduct = totalDeduct.add(record.getDeduct());
        }
        
        summary.setTotalIncome(totalIncome.setScale(2, RoundingMode.HALF_UP));
        summary.setTotalTax(totalTax.setScale(2, RoundingMode.HALF_UP));
        summary.setTotalDeduct(totalDeduct.setScale(2, RoundingMode.HALF_UP));
        
        BigDecimal averageTaxRate = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            averageTaxRate = totalTax.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        summary.setAverageTaxRate(averageTaxRate.setScale(2, RoundingMode.HALF_UP));

        QueryWrapper<TaxRecord> lastYearWrapper = new QueryWrapper<>();
        lastYearWrapper.eq("userId", userId)
                .eq("taxYear", taxYear - 1)
                .eq("isDelete", 0);
        List<TaxRecord> lastYearRecords = taxRecordMapper.selectList(lastYearWrapper);
        
        BigDecimal lastYearIncome = BigDecimal.ZERO;
        BigDecimal lastYearTax = BigDecimal.ZERO;
        for (TaxRecord record : lastYearRecords) {
            if (record.getIncome() != null) lastYearIncome = lastYearIncome.add(record.getIncome());
            if (record.getTaxAmount() != null) lastYearTax = lastYearTax.add(record.getTaxAmount());
        }
        
        summary.setLastYearIncome(lastYearIncome.setScale(2, RoundingMode.HALF_UP));
        summary.setLastYearTax(lastYearTax.setScale(2, RoundingMode.HALF_UP));
        
        BigDecimal incomeGrowth = BigDecimal.ZERO;
        BigDecimal taxGrowth = BigDecimal.ZERO;
        if (lastYearIncome.compareTo(BigDecimal.ZERO) > 0) {
            incomeGrowth = totalIncome.subtract(lastYearIncome)
                    .divide(lastYearIncome, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        if (lastYearTax.compareTo(BigDecimal.ZERO) > 0) {
            taxGrowth = totalTax.subtract(lastYearTax)
                    .divide(lastYearTax, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        
        summary.setIncomeGrowth(incomeGrowth.setScale(2, RoundingMode.HALF_UP));
        summary.setTaxGrowth(taxGrowth.setScale(2, RoundingMode.HALF_UP));
        
        return summary;
    }

    @Override
    public List<AnalysisVO.MonthlyTrend> getUserMonthlyTrend(Long userId, Integer taxYear) {
        List<AnalysisVO.MonthlyTrend> trends = new ArrayList<>();
        
        QueryWrapper<TaxRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("taxYear", taxYear)
                .eq("isDelete", 0)
                .ne("taxMonth", 0)
                .orderByAsc("taxMonth");
        
        List<TaxRecord> records = taxRecordMapper.selectList(queryWrapper);
        
        Map<Integer, AnalysisVO.MonthlyTrend> monthMap = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            AnalysisVO.MonthlyTrend trend = new AnalysisVO.MonthlyTrend();
            trend.setMonth(i);
            trend.setMonthName(getMonthName(i));
            trend.setIncome(BigDecimal.ZERO);
            trend.setTax(BigDecimal.ZERO);
            trend.setDeduct(BigDecimal.ZERO);
            trend.setBeforeTaxIncome(BigDecimal.ZERO);
            monthMap.put(i, trend);
        }
        
        for (TaxRecord record : records) {
            AnalysisVO.MonthlyTrend trend = monthMap.get(record.getTaxMonth());
            if (trend != null) {
                if (record.getIncome() != null) trend.setIncome(record.getIncome());
                if (record.getTaxAmount() != null) trend.setTax(record.getTaxAmount());
                if (record.getDeduct() != null) trend.setDeduct(record.getDeduct());
                if (record.getBeforeTaxIncome() != null) {
                    trend.setBeforeTaxIncome(record.getBeforeTaxIncome());
                } else if (record.getIncome() != null && record.getInsurance() != null) {
                    trend.setBeforeTaxIncome(record.getIncome().subtract(record.getInsurance()));
                }
            }
        }
        
        trends.addAll(monthMap.values());
        return trends;
    }

    private String getMonthName(int month) {
        String[] names = {"", "一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"};
        return names[month];
    }

    @Override
    public List<AnalysisVO.DeductRatio> getUserDeductRatio(Long userId, Integer taxYear) {
        List<AnalysisVO.DeductRatio> ratios = new ArrayList<>();
        
        Map<Integer, BigDecimal> deductMap = new HashMap<>();
        Map<Integer, Long> countMap = new HashMap<>();
        
        QueryWrapper<TaxSpecialDeduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_delete", 0);
        
        List<TaxSpecialDeduct> deducts = taxSpecialDeductMapper.selectList(queryWrapper);
        
        BigDecimal totalDeduct = BigDecimal.ZERO;
        
        for (TaxSpecialDeduct deduct : deducts) {
            // 检查扣除是否在指定年度内有效
            if (!isDeductEffectiveInYear(deduct, taxYear)) {
                continue;
            }
            
            Integer type = deduct.getDeductType();
            BigDecimal amount = getDeductAmountByType(type, deduct.getId());
            
            deductMap.merge(type, amount, BigDecimal::add);
            countMap.merge(type, 1L, Long::sum);
            totalDeduct = totalDeduct.add(amount);
        }
        
        for (Map.Entry<Integer, BigDecimal> entry : deductMap.entrySet()) {
            AnalysisVO.DeductRatio ratio = new AnalysisVO.DeductRatio();
            ratio.setDeductType(String.valueOf(entry.getKey()));
            ratio.setDeductTypeName(DEDUCT_TYPE_MAP.getOrDefault(entry.getKey(), "未知"));
            ratio.setAmount(entry.getValue().setScale(2, RoundingMode.HALF_UP));
            ratio.setCount(countMap.get(entry.getKey()));
            
            BigDecimal percent = BigDecimal.ZERO;
            if (totalDeduct.compareTo(BigDecimal.ZERO) > 0) {
                percent = entry.getValue().divide(totalDeduct, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }
            ratio.setRatio(percent.setScale(2, RoundingMode.HALF_UP));
            
            ratios.add(ratio);
        }
        
        ratios.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        
        return ratios;
    }

    private BigDecimal getDeductAmountByType(Integer type, Long deductId) {
        BigDecimal amount = BigDecimal.ZERO;
        
        switch (type) {
            case 1:
                QueryWrapper<DeductChildEducation> ceWrapper = new QueryWrapper<>();
                ceWrapper.eq("deduct_id", deductId);
                DeductChildEducation ce = childEducationMapper.selectOne(ceWrapper);
                if (ce != null && ce.getMonthlyAmount() != null) {
                    amount = amount.add(ce.getMonthlyAmount());
                }
                break;
            case 2:
                QueryWrapper<DeductContinueEducation> ceeWrapper = new QueryWrapper<>();
                ceeWrapper.eq("deduct_id", deductId);
                DeductContinueEducation cee = continueEducationMapper.selectOne(ceeWrapper);
                if (cee != null && cee.getMonthlyAmount() != null) {
                    amount = amount.add(cee.getMonthlyAmount());
                }
                break;
            case 3:
                QueryWrapper<DeductSeriousIllness> siWrapper = new QueryWrapper<>();
                siWrapper.eq("deduct_id", deductId);
                DeductSeriousIllness si = seriousIllnessMapper.selectOne(siWrapper);
                if (si != null && si.getDeductibleAmount() != null) {
                    amount = amount.add(si.getDeductibleAmount());
                }
                break;
            case 4:
                QueryWrapper<DeductHouseLoan> hlWrapper = new QueryWrapper<>();
                hlWrapper.eq("deduct_id", deductId);
                DeductHouseLoan hl = houseLoanMapper.selectOne(hlWrapper);
                if (hl != null && hl.getMonthlyAmount() != null) {
                    amount = amount.add(hl.getMonthlyAmount());
                }
                break;
            case 5:
                QueryWrapper<DeductHouseRent> hrWrapper = new QueryWrapper<>();
                hrWrapper.eq("deduct_id", deductId);
                DeductHouseRent hr = houseRentMapper.selectOne(hrWrapper);
                if (hr != null && hr.getMonthlyAmount() != null) {
                    amount = amount.add(hr.getMonthlyAmount());
                }
                break;
            case 6:
                QueryWrapper<DeductElderSupport> esWrapper = new QueryWrapper<>();
                esWrapper.eq("deduct_id", deductId);
                DeductElderSupport es = elderSupportMapper.selectOne(esWrapper);
                if (es != null && es.getMonthlyAmount() != null) {
                    amount = amount.add(es.getMonthlyAmount());
                }
                break;
            default:
                break;
        }
        
        return amount;
    }

    /**
     * 检查专项附加扣除是否在指定年度内有效
     */
    private boolean isDeductEffectiveInYear(TaxSpecialDeduct deduct, Integer taxYear) {
        Date startDate = deduct.getStartDate();
        Date endDate = deduct.getEndDate();
        
        // 创建年度的起止日期
        Calendar yearStart = Calendar.getInstance();
        yearStart.set(taxYear, 0, 1, 0, 0, 0);
        Date yearStartDate = yearStart.getTime();
        
        Calendar yearEnd = Calendar.getInstance();
        yearEnd.set(taxYear, 11, 31, 23, 59, 59);
        Date yearEndDate = yearEnd.getTime();
        
        // 检查扣除是否与年度有重叠
        boolean beforeYear = (endDate != null && endDate.before(yearStartDate));
        boolean afterYear = (startDate != null && startDate.after(yearEndDate));
        
        return !beforeYear && !afterYear;
    }

    @Override
    public AnalysisVO.TaxAnalysisReport generateTaxReport(Long userId, Integer taxYear) {
        AnalysisVO.TaxAnalysisReport report = new AnalysisVO.TaxAnalysisReport();
        report.setReportDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        
        AnalysisVO.AnnualSummary summary = getUserAnnualSummary(userId, taxYear);
        report.setTotalIncome(summary.getTotalIncome());
        report.setTotalTax(summary.getTotalTax());
        report.setEffectiveTaxRate(summary.getAverageTaxRate());
        
        BigDecimal taxRate = summary.getAverageTaxRate();
        String taxLevel;
        if (taxRate.compareTo(new BigDecimal("5")) <= 0) {
            taxLevel = "低税负";
        } else if (taxRate.compareTo(new BigDecimal("15")) <= 0) {
            taxLevel = "中等税负";
        } else if (taxRate.compareTo(new BigDecimal("25")) <= 0) {
            taxLevel = "较高税负";
        } else {
            taxLevel = "高税负";
        }
        report.setTaxLevel(taxLevel);
        
        List<String> suggestions = new ArrayList<>();
        
        if (taxRate.compareTo(new BigDecimal("20")) > 0) {
            suggestions.add("您的税负较高，建议充分利用专项附加扣除政策进行优化");
        }
        
        List<AnalysisVO.DeductRatio> deductRatios = getUserDeductRatio(userId, taxYear);
        Set<Integer> usedTypes = deductRatios.stream()
                .map(r -> Integer.parseInt(r.getDeductType()))
                .collect(Collectors.toSet());
        
        if (!usedTypes.contains(1)) {
            suggestions.add("如果您有子女正在接受教育，建议添加子女教育专项附加扣除");
        }
        if (!usedTypes.contains(2)) {
            suggestions.add("如果您正在接受继续教育，建议添加继续教育专项附加扣除");
        }
        if (!usedTypes.contains(4) && !usedTypes.contains(5)) {
            suggestions.add("如果您有住房贷款或租房，建议添加住房贷款利息或住房租金专项附加扣除");
        }
        if (!usedTypes.contains(6)) {
            suggestions.add("如果您需要赡养老人，建议添加赡养老人专项附加扣除");
        }
        
        if (summary.getTotalDeduct().compareTo(BigDecimal.ZERO) > 0) {
            suggestions.add("您已享受专项附加扣除，扣除金额为 ¥" + summary.getTotalDeduct());
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("您的税务情况良好，继续保持！");
        }
        
        report.setSuggestions(suggestions);
        
        String summaryText = String.format("根据您%d年度的税务数据，全年收入¥%s，应缴税额¥%s，实际税负率%s%%。",
                taxYear,
                summary.getTotalIncome(),
                summary.getTotalTax(),
                summary.getAverageTaxRate());
        report.setSummary(summaryText);
        
        return report;
    }

    @Override
    public AnalysisVO.PlatformStats getPlatformStats() {
        AnalysisVO.PlatformStats stats = new AnalysisVO.PlatformStats();
        
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("isDelete", 0);
        stats.setTotalUsers(userMapper.selectCount(userWrapper));

        LocalDate today = LocalDate.now();
        QueryWrapper<User> todayUserWrapper = new QueryWrapper<>();
        todayUserWrapper.eq("isDelete", 0)
                .ge("createTime", today.atStartOfDay())
                .le("createTime", today.plusDays(1).atStartOfDay());
        stats.setTodayNewUsers(userMapper.selectCount(todayUserWrapper));

        QueryWrapper<TaxRecord> taxWrapper = new QueryWrapper<>();
        taxWrapper.eq("isDelete", 0);
        List<TaxRecord> allRecords = taxRecordMapper.selectList(taxWrapper);
        BigDecimal totalTax = BigDecimal.ZERO;
        for (TaxRecord record : allRecords) {
            if (record.getTaxAmount() != null) {
                totalTax = totalTax.add(record.getTaxAmount());
            }
        }
        stats.setTotalTaxAmount(totalTax.setScale(2, RoundingMode.HALF_UP));
        
        long userCount = stats.getTotalUsers();
        if (userCount > 0) {
            BigDecimal avgTax = totalTax.divide(new BigDecimal(userCount), 2, RoundingMode.HALF_UP);
            stats.setAvgTaxPerUser(avgTax);
        } else {
            stats.setAvgTaxPerUser(BigDecimal.ZERO);
        }
        
        stats.setTotalRecords((long) allRecords.size());
        
        QueryWrapper<TaxSpecialDeduct> deductWrapper = new QueryWrapper<>();
        deductWrapper.eq("status", 1).eq("is_delete", 0);
        long deductCount = taxSpecialDeductMapper.selectCount(deductWrapper);
        BigDecimal deductUsageRate = BigDecimal.ZERO;
        if (userCount > 0) {
            deductUsageRate = new BigDecimal(deductCount)
                    .divide(new BigDecimal(userCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        stats.setDeductUsageRate(deductUsageRate.setScale(2, RoundingMode.HALF_UP));
        
        QueryWrapper<User> activeUserWrapper = new QueryWrapper<>();
        activeUserWrapper.eq("isDelete", 0);
        long activeUsers = userMapper.selectCount(activeUserWrapper);
        stats.setActiveUsers(activeUsers);
        
        BigDecimal totalDeductAmount = BigDecimal.ZERO;
        List<TaxSpecialDeduct> deductList = taxSpecialDeductMapper.selectList(deductWrapper);
        for (TaxSpecialDeduct deduct : deductList) {
            BigDecimal amount = getDeductAmountByType(deduct.getDeductType(), deduct.getId());
            totalDeductAmount = totalDeductAmount.add(amount);
        }
        BigDecimal avgDeductAmount = BigDecimal.ZERO;
        if (deductCount > 0) {
            avgDeductAmount = totalDeductAmount.divide(new BigDecimal(deductCount), 2, RoundingMode.HALF_UP);
        }
        stats.setAvgDeductAmount(avgDeductAmount);
        
        return stats;
    }

    @Override
    public List<AnalysisVO.IncomeDistribution> getIncomeDistribution() {
        List<AnalysisVO.IncomeDistribution> distributions = new ArrayList<>();
        
        long range1 = taxRecordMapper.countByIncomeRange(BigDecimal.ZERO, new BigDecimal("5000"));
        long range2 = taxRecordMapper.countByIncomeRange(new BigDecimal("5000"), new BigDecimal("10000"));
        long range3 = taxRecordMapper.countByIncomeRange(new BigDecimal("10000"), new BigDecimal("20000"));
        long range4 = taxRecordMapper.countByIncomeRange(new BigDecimal("20000"), new BigDecimal("50000"));
        long range5 = taxRecordMapper.countByIncomeRange(new BigDecimal("50000"), null);
        
        distributions.add(createIncomeDistribution("0-5000", range1));
        distributions.add(createIncomeDistribution("5000-10000", range2));
        distributions.add(createIncomeDistribution("10000-20000", range3));
        distributions.add(createIncomeDistribution("20000-50000", range4));
        distributions.add(createIncomeDistribution("50000+", range5));
        
        long total = distributions.stream().mapToLong(AnalysisVO.IncomeDistribution::getUserCount).sum();
        
        for (AnalysisVO.IncomeDistribution dist : distributions) {
            BigDecimal proportion = BigDecimal.ZERO;
            if (total > 0) {
                proportion = new BigDecimal(dist.getUserCount())
                        .divide(new BigDecimal(total), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }
            dist.setProportion(proportion.setScale(2, RoundingMode.HALF_UP));
        }
        
        return distributions;
    }
    
    private AnalysisVO.IncomeDistribution createIncomeDistribution(String range, long count) {
        AnalysisVO.IncomeDistribution dist = new AnalysisVO.IncomeDistribution();
        dist.setIncomeRange(range);
        dist.setUserCount(count);
        return dist;
    }

    @Override
    public List<AnalysisVO.CityTaxStats> getCityTaxStats() {
        List<AnalysisVO.CityTaxStats> stats = new ArrayList<>();
        
        List<TaxStatsVO.CityTaxSummary> citySummaries = taxRecordMapper.selectCityTaxSummary();
        
        Map<String, String> regionMap = new HashMap<>();
        regionMap.put("Beijing", "北京");
        regionMap.put("Shanghai", "上海");
        regionMap.put("Guangzhou", "广州");
        regionMap.put("Shenzhen", "深圳");
        regionMap.put("Hangzhou", "杭州");
        regionMap.put("Chengdu", "成都");
        regionMap.put("Wuhan", "武汉");
        regionMap.put("Nanjing", "南京");
        regionMap.put("Xi'an", "西安");
        regionMap.put("Chongqing", "重庆");
        
        for (TaxStatsVO.CityTaxSummary summary : citySummaries) {
            String cityName = regionMap.getOrDefault(summary.getCity(), summary.getCity());
            AnalysisVO.CityTaxStats stat = new AnalysisVO.CityTaxStats();
            stat.setCity(cityName);
            stat.setUserCount(summary.getUserCount());
            stat.setTotalTax(summary.getTotalTax() != null ? summary.getTotalTax().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            if (summary.getUserCount() > 0 && summary.getTotalTax() != null) {
                stat.setAvgTax(summary.getTotalTax().divide(new BigDecimal(summary.getUserCount()), 2, RoundingMode.HALF_UP));
            } else {
                stat.setAvgTax(BigDecimal.ZERO);
            }
            stats.add(stat);
        }
        
        stats.sort((a, b) -> b.getTotalTax().compareTo(a.getTotalTax()));
        
        return stats;
    }
}
