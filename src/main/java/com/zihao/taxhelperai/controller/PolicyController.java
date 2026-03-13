package com.zihao.taxhelperai.controller;

/**
 * @Author: 张梓豪
 * @CreateTime: 2026-03-13
 */
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.dto.guide.GuideAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyQueryDTO;
import com.zihao.taxhelperai.model.vo.PolicyVO;
import com.zihao.taxhelperai.service.PolicyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/policy")
public class PolicyController {

    @Resource
    private PolicyService policyService;

    /**
     * 分页查询政策（支持类型/关键词筛选）
     */
    @PostMapping("/list")
    public BaseResponse<Page<PolicyVO>> listPolicy(@Validated @RequestBody PolicyQueryDTO queryDTO) {
        Page<PolicyVO> policyPage = policyService.queryPolicyPage(queryDTO);
        return ResultUtils.success(policyPage);
    }

    /**
     * 根据id查询政策（含申报指引）
     */
    @GetMapping("/{id}")
    public BaseResponse<PolicyVO> getPolicyById(@PathVariable Integer id) {
        PolicyVO policyVO = policyService.getPolicyWithGuide(id);
        if (policyVO == null) {
            return ResultUtils.error(404, "政策不存在");
        }
        return ResultUtils.success(policyVO);
    }

    /**
     * 新增政策
     */
    @PostMapping("/add")
    public BaseResponse<Void> addPolicy(@Validated @RequestBody PolicyAddDTO addDTO) {
        boolean success = policyService.addPolicy(addDTO);
        if (success) {
            return ResultUtils.success(null);
        } else {
            return ResultUtils.error(500, "新增政策失败");
        }
    }

    /**
     * 新增申报指引（关联政策）
     */
    @PostMapping("/guide/add")
    public BaseResponse<Void> addGuide(@Validated @RequestBody GuideAddDTO addDTO) {
        boolean success = policyService.addGuide(addDTO);
        if (success) {
            return ResultUtils.success(null);
        } else {
            return ResultUtils.error(500, "新增申报指引失败");
        }
    }

    /**
     * 逻辑删除政策（级联删除申报指引）
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deletePolicy(@PathVariable Integer id) {
        boolean success = policyService.deletePolicy(id);
        if (success) {
            return ResultUtils.success(null);
        } else {
            return ResultUtils.error(500, "删除政策失败");
        }
    }
}
