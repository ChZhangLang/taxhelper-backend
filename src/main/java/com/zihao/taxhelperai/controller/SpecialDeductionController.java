package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.DeleteRequest;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionAddRequest;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionEditRequest;
import com.zihao.taxhelperai.model.dto.specialDeduction.SpecialDeductionQueryRequest;
import com.zihao.taxhelperai.model.entity.SpecialDeduction;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.SpecialDeductionVO;
import com.zihao.taxhelperai.service.SpecialDeductionService;
import com.zihao.taxhelperai.annotation.AuthCheck;
import com.zihao.taxhelperai.constant.UserConstant;
import com.zihao.taxhelperai.model.vo.LoginUserVO;
import com.zihao.taxhelperai.service.UserService;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * 专项附加扣除控制器
 */
@RestController
@RequestMapping("/special-deduction")
public class SpecialDeductionController {

    @Resource
    private SpecialDeductionService specialDeductionService;

    @Resource
    private UserService userService;

    /**
     * 添加专项附加扣除
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<SpecialDeduction> addSpecialDeduction(@RequestBody SpecialDeductionAddRequest request, HttpServletRequest httpServletRequest) {
        User User = userService.getLoginUser(httpServletRequest);
        LoginUserVO loginUser = userService.getLoginUserVO(User);
        SpecialDeduction specialDeduction = specialDeductionService.addSpecialDeduction(request, loginUser.getId());
        return ResultUtils.success(specialDeduction);
    }

    /**
     * 编辑专项附加扣除
     */
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<SpecialDeduction> editSpecialDeduction(@RequestBody SpecialDeductionEditRequest request, HttpServletRequest httpServletRequest) {
        User User = userService.getLoginUser(httpServletRequest);
        LoginUserVO loginUser = userService.getLoginUserVO(User);
        SpecialDeduction specialDeduction = specialDeductionService.editSpecialDeduction(request, loginUser.getId());
        return ResultUtils.success(specialDeduction);
    }

    /**
     * 删除专项附加扣除
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<Boolean> deleteSpecialDeduction(@RequestBody DeleteRequest request, HttpServletRequest httpServletRequest) {
        User User = userService.getLoginUser(httpServletRequest);
        LoginUserVO loginUser = userService.getLoginUserVO(User);
        boolean result = specialDeductionService.deleteSpecialDeduction(request.getId(), loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 获取专项附加扣除详情
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<SpecialDeductionVO> getSpecialDeduction(@RequestParam Long id, HttpServletRequest httpServletRequest) {
        User User = userService.getLoginUser(httpServletRequest);
        LoginUserVO loginUser = userService.getLoginUserVO(User);
        SpecialDeductionVO specialDeductionVO = specialDeductionService.getSpecialDeductionById(id, loginUser.getId());
        return ResultUtils.success(specialDeductionVO);
    }

    /**
     * 分页查询专项附加扣除列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<Page<SpecialDeductionVO>> listSpecialDeductionByPage(@RequestBody SpecialDeductionQueryRequest request, HttpServletRequest httpServletRequest) {
        User User = userService.getLoginUser(httpServletRequest);
        LoginUserVO loginUser = userService.getLoginUserVO(User);
        Page<SpecialDeductionVO> page = specialDeductionService.listSpecialDeductionVOByPage(request, loginUser.getId());
        return ResultUtils.success(page);
    }

    /**
     * 获取用户当前生效的专项附加扣除总额
     */
    @GetMapping("/current-amount")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<BigDecimal> getCurrentDeductionAmount(HttpServletRequest httpServletRequest) {
        User User = userService.getLoginUser(httpServletRequest);
        LoginUserVO loginUser = userService.getLoginUserVO(User);
        BigDecimal amount = specialDeductionService.getCurrentDeductionAmount(loginUser.getId());
        return ResultUtils.success(amount);
    }

    /**
     * 更新专项附加扣除状态（定时任务调用）
     */
    @PostMapping("/update-status")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Void> updateDeductionStatus() {
        specialDeductionService.updateDeductionStatus();
        return ResultUtils.success(null);
    }
}
