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
import com.zihao.taxhelperai.service.TaxRecordService;
import com.zihao.taxhelperai.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
     * 计算个税并保存记录
     *
     * @param taxCalculateRequest 计算请求
     * @param request 请求上下文（获取登录用户）
     * @return 计算结果
     */
    @PostMapping("/calculate")
    public BaseResponse<TaxCalculateVO> calculateTax(
            @Valid @RequestBody TaxCalculateRequest taxCalculateRequest,
            HttpServletRequest request) {
        // 1. 获取登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        // 2. 执行计算并保存记录
        TaxCalculateVO taxCalculateVO = taxRecordService.calculateAndSaveTax(taxCalculateRequest, loginUser.getId());
        return ResultUtils.success(taxCalculateVO);
    }

    /**
     * 查询当前用户的计税记录（分页）
     *
     * @param taxRecordQueryRequest 查询条件
     * @param request 请求上下文
     * @return 分页记录
     */
    @PostMapping("/record/my/page")
    public BaseResponse<Page<TaxRecordVO>> listMyTaxRecordByPage(
            @RequestBody TaxRecordQueryRequest taxRecordQueryRequest,
            HttpServletRequest request) {
        // 1. 获取登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");

        // 2. 只查自己的记录
        taxRecordQueryRequest.setUserId(loginUser.getId());
        Page<TaxRecordVO> taxRecordVOPage = taxRecordService.listTaxRecordVOByPage(taxRecordQueryRequest);
        return ResultUtils.success(taxRecordVOPage);
    }

    /**
     * 管理员查询所有用户的计税记录（分页）
     *
     * @param taxRecordQueryRequest 查询条件
     * @param request 请求上下文
     * @return 分页记录
     */
    @PostMapping("/record/admin/page")
    public BaseResponse<Page<TaxRecordVO>> listAdminTaxRecordByPage(
            @RequestBody TaxRecordQueryRequest taxRecordQueryRequest,
            HttpServletRequest request) {
        // 1. 校验管理员权限
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可查询所有记录");
        }
        // 2. 分页查询
        Page<TaxRecordVO> taxRecordVOPage = taxRecordService.listTaxRecordVOByPage(taxRecordQueryRequest);
        return ResultUtils.success(taxRecordVOPage);
    }

    /**
     * 获取税收统计分析数据（仅管理员）
     *
     * @param request 请求上下文
     * @return 统计VO
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
     *
     * @param id 记录ID
     * @param request 请求上下文
     * @return 操作结果
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