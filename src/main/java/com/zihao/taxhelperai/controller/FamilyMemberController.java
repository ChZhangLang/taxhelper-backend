package com.zihao.taxhelperai.controller;

import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.entity.FamilyMember;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.service.FamilyMemberService;
import com.zihao.taxhelperai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 家属信息控制器
 */
@RestController
@RequestMapping("/family-member")
@Slf4j
public class FamilyMemberController {

    @Resource
    private FamilyMemberService familyMemberService;

    @Resource
    private UserService userService;

    /**
     * 获取当前用户的家属列表
     */
    @GetMapping("/list")
    public BaseResponse<List<FamilyMember>> getFamilyMemberList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<FamilyMember> list = familyMemberService.getByUserId(loginUser.getId());
        return ResultUtils.success(list);
    }

    /**
     * 添加家属信息
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addFamilyMember(@RequestBody FamilyMember familyMember, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = familyMemberService.addFamilyMember(familyMember, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除家属信息
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFamilyMember(@RequestParam Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = familyMemberService.deleteFamilyMember(id, loginUser.getId());
        return ResultUtils.success(result);
    }
}