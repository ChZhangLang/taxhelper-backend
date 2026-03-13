package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.mapper.GuideMapper;
import com.zihao.taxhelperai.mapper.PolicyMapper;
import com.zihao.taxhelperai.model.dto.guide.GuideAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyQueryDTO;
import com.zihao.taxhelperai.model.entity.Guide;
import com.zihao.taxhelperai.model.entity.Policy;
import com.zihao.taxhelperai.model.vo.GuideVO;
import com.zihao.taxhelperai.model.vo.PolicyVO;
import com.zihao.taxhelperai.service.PolicyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl extends ServiceImpl<PolicyMapper, Policy> implements PolicyService {

    @Resource
    private GuideMapper guideMapper;

    /**
     * 分页查询政策（支持类型/关键词筛选）
     */
    @Override
    public Page<PolicyVO> queryPolicyPage(PolicyQueryDTO queryDTO) {
        // 1. 分页查询政策表
        Page<Policy> policyPage = new Page<>(queryDTO.getCurrent(), queryDTO.getPageSize());
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        // 类型筛选
        if (queryDTO.getType() != null) {
            wrapper.eq(Policy::getType, queryDTO.getType());
        }
        // 关键词模糊查询
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.like(Policy::getTitle, queryDTO.getKeyword());
        }
        // 排除逻辑删除的数据
        wrapper.eq(Policy::getIsDelete, 0);
        // 按创建时间倒序
        wrapper.orderByDesc(Policy::getCreateTime);
        page(policyPage, wrapper);

        // 2. 转换为VO（仅返回基础信息，申报指引在详情页查询）
        Page<PolicyVO> voPage = new Page<>();
        BeanUtils.copyProperties(policyPage, voPage);
        List<PolicyVO> voList = policyPage.getRecords().stream().map(policy -> {
            PolicyVO vo = new PolicyVO();
            BeanUtils.copyProperties(policy, vo);
            vo.setTypeDesc(policy.getType()); // 转换类型为中文
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 根据id查询政策+关联申报指引
     */
    @Override
    public PolicyVO getPolicyWithGuide(Integer policyId) {
        // 1. 查询政策基本信息
        Policy policy = getById(policyId);
        if (policy == null || policy.getIsDelete() == 1) {
            return null;
        }
        // 2. 转换为VO
        PolicyVO vo = new PolicyVO();
        BeanUtils.copyProperties(policy, vo);
        vo.setTypeDesc(policy.getType());

        // 3. 仅政策类型为3（申报流程）时，查询关联的申报指引
        if (policy.getType() == 3) {
            LambdaQueryWrapper<Guide> guideWrapper = new LambdaQueryWrapper<>();
            guideWrapper.eq(Guide::getPolicyId, policyId);
            guideWrapper.eq(Guide::getIsDelete, 0);
            List<Guide> guideList = guideMapper.selectList(guideWrapper);
            // 转换为GuideVO
            List<GuideVO> guideVOList = guideList.stream().map(guide -> {
                GuideVO guideVO = new GuideVO();
                BeanUtils.copyProperties(guide, guideVO);
                return guideVO;
            }).collect(Collectors.toList());
            vo.setGuideList(guideVOList);
        }
        return vo;
    }

    /**
     * 新增政策
     */
    @Override
    public boolean addPolicy(PolicyAddDTO addDTO) {
        Policy policy = new Policy();
        BeanUtils.copyProperties(addDTO, policy);
        // MyBatis-Plus自动填充创建/更新时间（需配置元对象处理器）
        return save(policy);
    }

    /**
     * 新增申报指引
     */
    @Override
    public boolean addGuide(GuideAddDTO addDTO) {
        // 先校验政策是否存在
        Policy policy = getById(addDTO.getPolicyId());
        if (policy == null || policy.getIsDelete() == 1) {
            return false;
        }
        Guide guide = new Guide();
        BeanUtils.copyProperties(addDTO, guide);
        return guideMapper.insert(guide) > 0;
    }

    /**
     * 逻辑删除政策（级联删除申报指引）
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务保证原子性
    public boolean deletePolicy(Integer policyId) {
        // 1. 逻辑删除政策
        boolean policyDel = removeById(policyId);
        if (!policyDel) {
            return false;
        }
        // 2. 级联逻辑删除申报指引
        LambdaQueryWrapper<Guide> guideWrapper = new LambdaQueryWrapper<>();
        guideWrapper.eq(Guide::getPolicyId, policyId);
        guideMapper.delete(guideWrapper);
        return true;
    }
}