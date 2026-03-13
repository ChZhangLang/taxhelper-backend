package com.zihao.taxhelperai.service;

import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author MI
* @description 针对表【policy(税务政策表)】的数据库操作Service
* @createDate 2026-03-13 09:42:25
*/
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihao.taxhelperai.model.dto.guide.GuideAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyQueryDTO;
import com.zihao.taxhelperai.model.entity.Policy;
import com.zihao.taxhelperai.model.vo.PolicyVO;

public interface PolicyService extends IService<Policy> {
    /**
     * 分页查询政策（含筛选）
     */
    Page<PolicyVO> queryPolicyPage(PolicyQueryDTO queryDTO);

    /**
     * 根据id查询政策+关联申报指引
     */
    PolicyVO getPolicyWithGuide(Integer policyId);

    /**
     * 新增政策
     */
    boolean addPolicy(PolicyAddDTO addDTO);

    /**
     * 新增申报指引
     */
    boolean addGuide(GuideAddDTO addDTO);

    /**
     * 逻辑删除政策（级联删除指引）
     */
    boolean deletePolicy(Integer policyId);
}
