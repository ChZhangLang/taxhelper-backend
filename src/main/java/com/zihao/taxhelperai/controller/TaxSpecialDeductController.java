package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.dto.specialDeduct.*;
import com.zihao.taxhelperai.model.entity.TaxSpecialDeduct;
import com.zihao.taxhelperai.model.vo.TaxSpecialDeductVO;
import com.zihao.taxhelperai.service.TaxSpecialDeductService;
import com.zihao.taxhelperai.service.UserService;
import com.zihao.taxhelperai.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tax/special-deduct")
public class TaxSpecialDeductController {

    private final TaxSpecialDeductService taxSpecialDeductService;
    private final UserService userService;

    @Autowired
    public TaxSpecialDeductController(TaxSpecialDeductService taxSpecialDeductService, UserService userService) {
        this.taxSpecialDeductService = taxSpecialDeductService;
        this.userService = userService;
    }

    @PostMapping("/child-education")
    public BaseResponse<TaxSpecialDeduct> addChildEducation(@RequestBody ChildEducationDTO dto, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxSpecialDeduct deduct = taxSpecialDeductService.addChildEducation(user.getId(), dto);
        return ResultUtils.success(deduct);
    }

    @PostMapping("/house-loan")
    public BaseResponse<TaxSpecialDeduct> addHouseLoan(@RequestBody HouseLoanDTO dto, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxSpecialDeduct deduct = taxSpecialDeductService.addHouseLoan(user.getId(), dto);
        return ResultUtils.success(deduct);
    }

    @PostMapping("/house-rent")
    public BaseResponse<TaxSpecialDeduct> addHouseRent(@RequestBody HouseRentDTO dto, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxSpecialDeduct deduct = taxSpecialDeductService.addHouseRent(user.getId(), dto);
        return ResultUtils.success(deduct);
    }

    @PostMapping("/elder-support")
    public BaseResponse<TaxSpecialDeduct> addElderSupport(@RequestBody ElderSupportDTO dto, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxSpecialDeduct deduct = taxSpecialDeductService.addElderSupport(user.getId(), dto);
        return ResultUtils.success(deduct);
    }

    @PostMapping("/continue-education")
    public BaseResponse<TaxSpecialDeduct> addContinueEducation(@RequestBody ContinueEducationDTO dto, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxSpecialDeduct deduct = taxSpecialDeductService.addContinueEducation(user.getId(), dto);
        return ResultUtils.success(deduct);
    }

    @PostMapping("/serious-illness")
    public BaseResponse<TaxSpecialDeduct> addSeriousIllness(@RequestBody SeriousIllnessDTO dto, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxSpecialDeduct deduct = taxSpecialDeductService.addSeriousIllness(user.getId(), dto);
        return ResultUtils.success(deduct);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteDeduct(@PathVariable Long id, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        boolean result = taxSpecialDeductService.deleteDeduct(id, user.getId());
        return ResultUtils.success(result);
    }

    @GetMapping("/list")
    public BaseResponse<List<TaxSpecialDeductVO>> listByUserId(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        List<TaxSpecialDeductVO> list = taxSpecialDeductService.listByUserId(user.getId());
        return ResultUtils.success(list);
    }

    @GetMapping("/amount")
    public BaseResponse<BigDecimal> getCurrentDeductAmount(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        BigDecimal amount;
        
        // 如果提供了年份和月份，获取该年月的扣除金额
        if (year != null && month != null) {
            amount = taxSpecialDeductService.getDeductAmountByYearMonth(user.getId(), year, month);
        } else {
            // 否则获取当前生效的扣除金额
            amount = taxSpecialDeductService.getCurrentDeductAmount(user.getId());
        }
        
        return ResultUtils.success(amount);
    }
}