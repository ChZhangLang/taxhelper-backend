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
import com.zihao.taxhelperai.model.vo.PolicySyncVO;
import com.zihao.taxhelperai.model.vo.PolicyVO;
import com.zihao.taxhelperai.service.PolicyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePolicy(Integer policyId) {
        boolean policyDel = removeById(policyId);
        if (!policyDel) {
            return false;
        }
        LambdaQueryWrapper<Guide> guideWrapper = new LambdaQueryWrapper<>();
        guideWrapper.eq(Guide::getPolicyId, policyId);
        guideMapper.delete(guideWrapper);
        return true;
    }

    /**
     * 手动同步政策（假爬虫实现）
     * 模拟从税务网站抓取最新政策数据
     */
    @Override
    public PolicySyncVO syncPolicy() {
        try {
            Thread.sleep(1000 + new Random().nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<Policy> mockData = createMockPolicies();
        Set<String> existingTitles = getExistingTitles();

        List<Policy> newPolicies = new ArrayList<>();
        for (Policy policy : mockData) {
            if (!existingTitles.contains(policy.getTitle())) {
                newPolicies.add(policy);
                existingTitles.add(policy.getTitle());
            }
        }

        int insertCount = 0;
        for (Policy policy : newPolicies) {
            if (save(policy)) {
                insertCount++;
            }
        }

        return PolicySyncVO.success(insertCount);
    }

    private Set<String> getExistingTitles() {
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Policy::getTitle);
        wrapper.eq(Policy::getIsDelete, 0);
        List<Policy> existing = list(wrapper);
        Set<String> titles = new HashSet<>();
        for (Policy p : existing) {
            titles.add(p.getTitle());
        }
        return titles;
    }

    private List<Policy> createMockPolicies() {
        return Arrays.asList(
            createPolicy("关于实施个人所得税专项附加扣除标准的通知", 2,
                "根据国务院部署，进一步提高个人所得税专项附加扣除标准。自2023年1月1日起，子女教育、继续教育、大病医疗、住房贷款利息、住房租金、赡养老人等专项附加扣除标准均有调整。"),
            createPolicy("关于完善个人所得税综合所得汇算清缴政策的公告", 1,
                "进一步明确个人所得税综合所得汇算清缴范围、计算方法及退税补税流程。纳税人需在每年3月1日至6月30日内完成汇算清缴。"),
            createPolicy("关于提高部分个人所得税税前扣除标准的通知", 2,
                "提高基本养老保险、基本医疗保险、失业保险等社会保险费和住房公积金的税前扣除标准，进一步减轻中低收入群体税收负担。"),
            createPolicy("关于办理个人所得税综合所得年度汇算清缴事项的公告", 3,
                "明确年度汇算清缴的办理时间、办理方式、所需材料及注意事项。纳税人可通过个人所得税APP、自然人电子税务局等渠道办理。"),
            createPolicy("关于支持居民换购住房有关个人所得税政策的通知", 1,
                "对出售自有住房并在现住房出售后1年内在市场重新购买住房的纳税人，对其出售现住房已缴纳的个人所得税予以退税优惠。"),
            createPolicy("关于提高个人所得税费用扣除标准的公告", 2,
                "个人所得税费用扣除标准（免征额）由每月5000元提高至每月6000元，进一步降低中低收入群体税负。"),
            createPolicy("关于继续实施个人所得税优惠政策的公告", 1,
                "延续实施个人养老金个人所得税优惠政策、上市公司股权激励优惠政策等多项个人所得税优惠政策至2027年底。"),
            createPolicy("关于开展个人所得税专项附加扣除信息核验工作的公告", 3,
                "税务机关将开展个人所得税专项附加扣除信息核验工作，请纳税人及时、准确填报专项附加扣除信息，确保享受税收优惠政策。"),
            createPolicy("关于进一步扶持自主就业退役士兵创业就业有关税收政策的公告", 1,
                "自主就业退役士兵从事个体经营的，3年内按每户每年限额依次扣减当年实际应缴纳的增值税、城市维护建设税、教育费附加、地方教育附加和个人所得税。"),
            createPolicy("关于提高个人所得税子女教育附加扣除标准的通知", 2,
                "子女教育专项附加扣除标准，由每个子女每月1000元提高至2000元。有多个子女的，可以分别扣除。"),
            createPolicy("关于提高个人所得税赡养老人专项附加扣除标准的通知", 2,
                "赡养老人专项附加扣除标准，由每月2000元提高至3000元。其中独生子女按照每月3000元标准定额扣除。"),
            createPolicy("关于提高个人所得税大病医疗专项附加扣除标准的通知", 2,
                "大病医疗专项附加扣除标准，由每年80000元提高至100000元。扣除医保报销后个人负担超过15000元的部分，准予扣除。"),
            createPolicy("关于办理个人所得税综合所得汇算清缴退税事项的公告", 3,
                "纳税人在年度汇算清缴中多缴税款的，可以申请退税。税务机关审核通过后，将按规定办理退税手续。"),
            createPolicy("关于加强高收入高净值人员个人所得税征收管理的公告", 1,
                "加强高收入高净值人员的个人所得税征收管理，重点关注股权转让、股息红利所得、偶然所得等收入项目的税收征管。"),
            createPolicy("关于进一步规范个人所得税扣缴申报的公告", 1,
                "扣缴义务人应当按照规定报送扣缴个人所得税报告表和相关信息，确保扣缴申报的真实性和准确性。"),
            createPolicy("关于开展个人所得税优惠政策精准推送工作的实施方案", 1,
                "建立个人所得税优惠政策精准推送机制，通过短信、APP推送等方式，主动向纳税人推送适用的税收优惠政策。"),
            createPolicy("关于优化个人所得税年度汇算清缴服务的通知", 3,
                "进一步优化年度汇算清缴服务，简化办理流程，压缩办理时间，为纳税人提供更加便捷高效的办税体验。"),
            createPolicy("关于继续执行个人所得税税收协定若干问题的公告", 1,
                "继续执行个人所得税税收协定的相关条款，明确跨境所得的税收管辖权和优惠适用条件。"),
            createPolicy("关于完善个人所得税住房贷款利息专项附加扣除政策的通知", 2,
                "住房贷款利息专项附加扣除标准不变，继续按每月1000元标准定额扣除。首套住房贷款利息支出，在实际发生贷款利息的年度扣除。")
        );
    }

    private Policy createPolicy(String title, Integer type, String content) {
        Policy policy = new Policy();
        policy.setTitle(title);
        policy.setType(type);
        policy.setContent(content);
        policy.setCreateUser(1);
        return policy;
    }
}