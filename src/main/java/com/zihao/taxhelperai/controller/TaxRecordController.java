package com.zihao.taxhelperai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.constant.UserConstant;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxCalculateRequest;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordQueryRequest;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.TaxCalculateVO;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;
import com.zihao.taxhelperai.model.vo.TaxStatsVO;
import com.zihao.taxhelperai.model.vo.TaxSettlementVO;
import com.zihao.taxhelperai.service.TaxRecordService;
import com.zihao.taxhelperai.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 个税计算控制器
 *
 * @author 你的名字
 */
@RestController
@RequestMapping("/tax")
public class TaxRecordController {

    @Resource
    private TaxRecordService taxRecordService;

    @Resource
    private UserService userService;

    /**
     * 计算个税并保存记录（原有方法，保留兼容）
     * POST /tax/calculate
     */
    @PostMapping("/calculate")
    public BaseResponse<TaxCalculateVO> calculateTax(
            @Valid @RequestBody TaxCalculateRequest taxCalculateRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        TaxCalculateVO taxCalculateVO = taxRecordService.calculateAndSaveTax(taxCalculateRequest, loginUser.getId());
        return ResultUtils.success(taxCalculateVO);
    }

    /**
     * 使用累计预扣法计算月度税额（新版）
     * POST /tax/calculate/monthly
     */
    @PostMapping("/calculate/monthly")
    public BaseResponse<TaxCalculateVO> calculateMonthlyTax(
            @Valid @RequestBody TaxCalculateRequest request,
            HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        TaxCalculateVO result = taxRecordService.calculateMonthlyWithCumulative(request, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 计算年度汇算清缴
     * GET /tax/settlement/{year}
     */
    @GetMapping("/settlement/{year}")
    public BaseResponse<TaxSettlementVO> calculateAnnualSettlement(
            @PathVariable Integer year,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        TaxSettlementVO result = taxRecordService.calculateAnnualSettlement(loginUser.getId(), year);
        return ResultUtils.success(result);
    }

    /**
     * 获取当年累计数据
     * GET /tax/cumulative?year=2024
     */
    @GetMapping("/cumulative")
    public BaseResponse<Map<String, BigDecimal>> getCumulativeData(
            @RequestParam Integer year,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Map<String, BigDecimal> data = taxRecordService.getCumulativeData(loginUser.getId(), year);
        return ResultUtils.success(data);
    }

    /**
     * 查询当前用户的计税记录（分页）
     * POST /tax/record/my/page
     */
    @PostMapping("/record/my/page")
    public BaseResponse<Page<TaxRecordVO>> listMyTaxRecordByPage(
            @RequestBody TaxRecordQueryRequest taxRecordQueryRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        taxRecordQueryRequest.setUserId(loginUser.getId());
        Page<TaxRecordVO> taxRecordVOPage = taxRecordService.listTaxRecordVOByPage(taxRecordQueryRequest);
        return ResultUtils.success(taxRecordVOPage);
    }

    /**
     * 管理员查询所有用户的计税记录（分页）
     * POST /tax/record/admin/page
     */
    @PostMapping("/record/admin/page")
    public BaseResponse<Page<TaxRecordVO>> listAdminTaxRecordByPage(
            @RequestBody TaxRecordQueryRequest taxRecordQueryRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可查询所有记录");
        }
        Page<TaxRecordVO> taxRecordVOPage = taxRecordService.listTaxRecordVOByPage(taxRecordQueryRequest);
        return ResultUtils.success(taxRecordVOPage);
    }

    /**
     * 获取税收统计分析数据（仅管理员）
     * GET /tax/record/stats
     */
    @GetMapping("/record/stats")
    public BaseResponse<TaxStatsVO> getTaxStats(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可查看统计数据");
        }
        TaxStatsVO statsVO = taxRecordService.getTaxStats();
        return ResultUtils.success(statsVO);
    }

    /**
     * 删除计税记录（仅管理员）
     * DELETE /tax/record/{id}
     */
    @DeleteMapping("/record/{id}")
    public BaseResponse<Boolean> deleteTaxRecord(
            @PathVariable Long id,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可删除记录");
        }
        boolean result = taxRecordService.removeById(id);
        return ResultUtils.success(result);
    }
}