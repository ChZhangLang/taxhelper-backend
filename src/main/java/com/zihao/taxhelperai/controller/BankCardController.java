package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.entity.BankCard;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.service.BankCardService;
import com.zihao.taxhelperai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 银行卡信息控制器
 */
@RestController
@RequestMapping("/bank-card")
@Slf4j
public class BankCardController {

    @Resource
    private BankCardService bankCardService;

    @Resource
    private UserService userService;

    /**
     * 获取当前用户的银行卡列表
     */
    @GetMapping("/list")
    public BaseResponse<List<BankCard>> getBankCardList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<BankCard> list = bankCardService.getByUserId(loginUser.getId());
        return ResultUtils.success(list);
    }

    /**
     * 添加银行卡信息
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addBankCard(@RequestBody BankCard bankCard, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = bankCardService.addBankCard(bankCard, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除银行卡信息
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBankCard(@RequestParam Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = bankCardService.deleteBankCard(id, loginUser.getId());
        return ResultUtils.success(result);
    }
}