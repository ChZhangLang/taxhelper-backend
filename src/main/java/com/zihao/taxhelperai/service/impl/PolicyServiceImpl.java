package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.mapper.GuideMapper;
import com.zihao.taxhelperai.mapper.PolicyMapper;
import com.zihao.taxhelperai.model.dto.guide.GuideAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyAddDTO;
import com.zihao.taxhelperai.model.dto.policy.PolicyQueryDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zihao.taxhelperai.ai.AiService;
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

    @Resource
    private AiService aiService;

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
        // 调用AI解析政策
        fillAiInfo(policy);
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
            // 调用AI解析政策
            fillAiInfo(policy);
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
            // ========== 基础政策类 (type=1) ==========
            createPolicy("关于延续实施全年一次性奖金个人所得税优惠政策的公告", 1,
                "居民个人取得全年一次性奖金，符合《国家税务总局关于调整个人取得全年一次性奖金等计算征收个人所得税方法问题的通知》规定的，不并入当年综合所得，以全年一次性奖金收入除以12个月得到的数额，按照本公告所附按月换算后的综合所得税率表，确定适用税率和速算扣除数，单独计算纳税。本政策执行至2027年12月31日。"),
            createPolicy("关于支持居民换购住房有关个人所得税政策的公告", 1,
                "自2024年1月1日至2025年12月31日，对出售自有住房并在现住房出售后1年内在市场重新购买住房的纳税人，对其出售现住房已缴纳的个人所得税予以退税优惠。"),
            createPolicy("关于进一步扶持自主就业退役士兵创业就业有关税收政策的公告", 1,
                "自主就业退役士兵从事个体经营的，自办理个体工商户登记当月起，在3年（36个月）内按每户每年20000元为限额依次扣减其当年实际应缴纳的增值税、城市维护建设税、教育费附加、地方教育附加和个人所得税。"),
            createPolicy("关于继续实施个人养老金个人所得税优惠政策的公告", 1,
                "自2024年1月1日起，对个人养老金实施递延纳税优惠政策。在缴费环节，个人向个人养老金资金账户的缴费，按照12000元/年的限额标准，在综合所得或经营所得中据实扣除；在投资环节，计入个人养老金资金账户的投资收益暂不征收个人所得税；在领取环节，个人领取的个人养老金，不并入综合所得，单独按照3%的税率计算缴纳个人所得税。"),
            createPolicy("关于延续实施上市公司股权激励有关个人所得税政策的公告", 1,
                "居民个人取得股票期权、股票增值权、限制性股票、股权奖励等股权激励，符合相关规定条件的，不并入当年综合所得，全额单独适用综合所得税率表，计算纳税。本政策执行至2027年12月31日。"),
            createPolicy("关于加强高收入高净值人员个人所得税征收管理的公告", 1,
                "加强高收入高净值人员的个人所得税征收管理，重点关注股权转让、股息红利所得、财产租赁所得、偶然所得等收入项目的税收征管，严厉打击偷逃税行为。"),
            createPolicy("关于进一步规范个人所得税扣缴申报的公告", 1,
                "扣缴义务人应当按照规定报送扣缴个人所得税报告表和相关信息，确保扣缴申报的真实性和准确性。扣缴义务人未按规定履行扣缴义务的，将依法承担相应法律责任。"),
            createPolicy("关于执行个人所得税税收协定若干问题的公告", 1,
                "继续执行个人所得税税收协定的相关条款，明确跨境所得的税收管辖权和优惠适用条件，避免双重征税，维护纳税人合法权益。"),
            createPolicy("关于个人所得税综合所得汇算清缴有关事项的公告", 1,
                "居民个人需要办理年度汇算的，应当在取得所得的次年3月1日至6月30日内，向任职、受雇单位所在地主管税务机关办理纳税申报，并报送《个人所得税年度自行纳税申报表》。"),
            createPolicy("关于明确个人所得税若干政策执行问题的通知", 1,
                "明确个人取得的各类补贴、津贴、奖金等收入的个人所得税处理方式，规范企业年金、职业年金的个税扣缴管理，完善股权激励和技术入股的税收政策。"),
            createPolicy("关于优化个人所得税预扣预缴办法的公告", 1,
                "对上一完整纳税年度内每月均在同一单位预扣预缴工资、薪金所得个人所得税且全年工资、薪金收入不超过6万元的居民个人，扣缴义务人在预扣预缴本年度工资、薪金所得个人所得税时，累计减除费用自1月份起直接按照全年6万元计算扣除。"),
            createPolicy("关于个人所得税专项附加扣除政策衔接问题的通知", 1,
                "明确专项附加扣除政策在新旧政策过渡期间的衔接办法，确保纳税人平稳享受税收优惠，避免出现政策断档或重复扣除。"),
            createPolicy("关于完善残疾人就业保障金制度更好促进残疾人就业的总体方案", 1,
                "落实残疾人就业个人所得税优惠政策，对残疾人个人取得的劳动所得，按照省、自治区、直辖市人民政府规定的减征幅度和期限减征个人所得税。"),
            createPolicy("关于科技人员取得职务科技成果转化现金奖励有关个人所得税政策的通知", 1,
                "依法批准设立的非营利性研究开发机构和高等学校根据《中华人民共和国促进科技成果转化法》规定，从职务科技成果转化收入中给予科技人员的现金奖励，可减按50%计入科技人员当月工资、薪金所得，依法缴纳个人所得税。"),
            createPolicy("关于支持和促进重点群体创业就业有关税收政策的通知", 1,
                "建档立卡贫困人口、持《就业创业证》或《就业失业登记证》的人员，从事个体经营的，自办理个体工商户登记当月起，在3年（36个月）内按每户每年12000元为限额依次扣减其当年实际应缴纳的增值税、城市维护建设税、教育费附加、地方教育附加和个人所得税。"),
            
            // ========== 扣除标准类 (type=2) ==========
            createPolicy("关于提高个人所得税费用扣除标准的公告", 2,
                "个人所得税费用扣除标准（免征额）为每月5000元，全年60000元。居民个人的综合所得，以每一纳税年度的收入额减除费用六万元以及专项扣除、专项附加扣除和依法确定的其他扣除后的余额，为应纳税所得额。"),
            createPolicy("关于提高个人所得税子女教育专项附加扣除标准的通知", 2,
                "子女教育专项附加扣除标准，由每个子女每月1000元提高至2000元。父母可以选择由其中一方按扣除标准的100%扣除，也可以选择由双方分别按扣除标准的50%扣除，具体扣除方式在一个纳税年度内不能变更。"),
            createPolicy("关于提高个人所得税赡养老人专项附加扣除标准的通知", 2,
                "赡养老人专项附加扣除标准，由每月2000元提高至3000元。其中，独生子女按照每月3000元标准定额扣除；非独生子女与兄弟姐妹分摊每月3000元的扣除额度，每人分摊的额度不能超过每月1500元。"),
            createPolicy("关于提高个人所得税大病医疗专项附加扣除标准的通知", 2,
                "大病医疗专项附加扣除标准，由每年80000元提高至100000元。在一个纳税年度内，纳税人发生的与基本医保相关的医药费用支出，扣除医保报销后个人负担（指医保目录范围内的自付部分）累计超过15000元的部分，由纳税人在办理年度汇算清缴时，在100000元限额内据实扣除。"),
            createPolicy("关于完善个人所得税住房贷款利息专项附加扣除政策的通知", 2,
                "住房贷款利息专项附加扣除标准为每月1000元，扣除期限最长不超过240个月。纳税人只能享受一次首套住房贷款的利息扣除。经夫妻双方约定，可以选择由其中一方扣除，具体扣除方式在一个纳税年度内不能变更。"),
            createPolicy("关于完善个人所得税住房租金专项附加扣除政策的通知", 2,
                "住房租金专项附加扣除标准：直辖市、省会（首府）城市、计划单列市以及国务院确定的其他城市，扣除标准为每月1500元；市辖区户籍人口超过100万的城市，扣除标准为每月1100元；市辖区户籍人口不超过100万的城市，扣除标准为每月800元。"),
            createPolicy("关于完善个人所得税继续教育专项附加扣除政策的通知", 2,
                "纳税人在中国境内接受学历（学位）继续教育的支出，在学历（学位）教育期间按照每月400元定额扣除。同一学历（学位）继续教育的扣除期限不能超过48个月。纳税人接受技能人员职业资格继续教育、专业技术人员职业资格继续教育的支出，在取得相关证书的当年，按照3600元定额扣除。"),
            createPolicy("关于提高个人所得税基本养老保险费扣除标准的通知", 2,
                "基本养老保险费、基本医疗保险费、失业保险费等社会保险费和住房公积金的个人缴纳部分，在规定范围内准予在个人所得税税前扣除。"),
            createPolicy("关于个人所得税专项附加扣除有关问题的公告", 2,
                "纳税人首次享受专项附加扣除，应当将专项附加扣除相关信息提交扣缴义务人或者税务机关，扣缴义务人应当及时将相关信息报送税务机关，纳税人对所提交信息的真实性、准确性、完整性负责。"),
            createPolicy("关于个人所得税专项附加扣除标准调整的通知", 2,
                "根据经济社会发展情况，适时调整专项附加扣除标准，进一步减轻纳税人负担，促进消费和民生改善。"),
            createPolicy("关于企业年金职业年金个人所得税有关问题的通知", 2,
                "企业和事业单位根据国家有关政策规定的办法和标准，为在本单位任职或者受雇的全体职工缴付的企业年金或职业年金单位缴费部分，在计入个人账户时，个人暂不缴纳个人所得税。"),
            createPolicy("关于个人所得税税前扣除有关问题的公告", 2,
                "明确个人所得税税前扣除的范围和标准，规范各类扣除项目的申报和管理，确保税收政策的准确执行。"),
            createPolicy("关于商业健康保险个人所得税政策试点的通知", 2,
                "对个人购买符合规定的商业健康保险产品的支出，允许在当年（月）计算应纳税所得额时予以税前扣除，扣除限额为2400元/年（200元/月）。"),
            createPolicy("关于公益慈善事业捐赠个人所得税政策的公告", 2,
                "个人通过中华人民共和国境内公益性社会组织、县级以上人民政府及其部门等国家机关，向教育、扶贫、济困等公益慈善事业的捐赠，发生的公益捐赠支出，可以按照个人所得税法有关规定在计算应纳税所得额时扣除。"),
            createPolicy("关于个人所得税专项附加扣除信息采集有关问题的公告", 2,
                "明确专项附加扣除信息采集的方式、流程和要求，规范信息报送和管理，保障纳税人合法权益。"),
            
            // ========== 申报流程类 (type=3) ==========
            createPolicy("关于办理个人所得税综合所得年度汇算清缴事项的公告", 3,
                "明确年度汇算清缴的办理时间为每年3月1日至6月30日，办理方式包括自行办理、通过扣缴义务人办理、委托涉税专业服务机构或其他单位及个人办理。纳税人可通过个人所得税APP、自然人电子税务局等渠道办理。"),
            createPolicy("关于开展个人所得税专项附加扣除信息核验工作的公告", 3,
                "税务机关将开展个人所得税专项附加扣除信息核验工作，请纳税人及时、准确填报专项附加扣除信息，确保享受税收优惠政策。对虚假申报的，将依法处理。"),
            createPolicy("关于办理个人所得税综合所得汇算清缴退税事项的公告", 3,
                "纳税人在年度汇算清缴中多缴税款的，可以申请退税。税务机关审核通过后，将按规定办理退税手续。退税申请一般在10个工作日内办结，特殊情况可能延长至30个工作日。"),
            createPolicy("关于优化个人所得税年度汇算清缴服务的通知", 3,
                "进一步优化年度汇算清缴服务，简化办理流程，压缩办理时间，为纳税人提供更加便捷高效的办税体验。推行智能申报，减少人工录入，提高申报准确性。"),
            createPolicy("关于个人所得税自行纳税申报有关问题的公告", 3,
                "明确个人所得税自行纳税申报的范围、期限、方式和要求，规范纳税人的申报行为，保障税收征管工作顺利开展。"),
            createPolicy("关于个人所得税扣缴申报管理办法的公告", 3,
                "规范个人所得税扣缴申报工作，明确扣缴义务人的权利和义务，加强扣缴申报的管理和监督，确保税款及时足额入库。"),
            createPolicy("关于电子税务局个人所得税申报功能优化的通知", 3,
                "优化电子税务局个人所得税申报功能，增加智能填报、自动计算、政策提醒等功能，提升纳税人办税体验。"),
            createPolicy("关于个人所得税年度汇算清缴辅导工作的通知", 3,
                "加强个人所得税年度汇算清缴辅导工作，通过线上线下多种方式，为纳税人提供政策解读和操作指导，帮助纳税人顺利完成汇算清缴。"),
            createPolicy("关于个人所得税专项附加扣除申报常见问题解答", 3,
                "解答个人所得税专项附加扣除申报过程中常见的问题，包括信息填写、扣除标准、申报流程等，帮助纳税人正确享受税收优惠政策。"),
            createPolicy("关于个人所得税申报信息更正有关问题的公告", 3,
                "明确个人所得税申报信息更正的条件、方式和流程，规范更正操作，保障纳税人申报信息的准确性。"),
            createPolicy("关于个人所得税完税证明开具有关问题的公告", 3,
                "明确个人所得税完税证明的开具方式和流程，纳税人可通过电子税务局、个人所得税APP等渠道查询和打印完税证明。"),
            createPolicy("关于个人所得税税收优惠备案有关问题的公告", 3,
                "明确个人所得税税收优惠备案的范围、方式和要求，简化备案流程，提高备案效率，方便纳税人享受税收优惠政策。"),
            createPolicy("关于个人所得税跨境所得申报有关问题的公告", 3,
                "明确居民个人取得境外所得的申报要求和流程，规范跨境所得的税收征管，避免双重征税。"),
            createPolicy("关于个人所得税年度汇算清缴补税有关问题的公告", 3,
                "纳税人在年度汇算清缴中需要补税的，应当在规定期限内缴纳税款。逾期未缴的，将依法加收滞纳金。"),
            createPolicy("关于个人所得税申报信用管理有关问题的公告", 3,
                "建立个人所得税申报信用管理制度，对纳税人的申报行为进行信用评价，对失信行为实施联合惩戒，促进纳税人诚信纳税。"),
            
            // ========== 更多基础政策 ==========
            createPolicy("关于个人所得税综合所得范围有关问题的公告", 1,
                "个人所得税综合所得包括工资、薪金所得，劳务报酬所得，稿酬所得，特许权使用费所得。居民个人取得综合所得，按纳税年度合并计算个人所得税。"),
            createPolicy("关于个人所得税经营所得征收管理有关问题的公告", 1,
                "个体工商户业主、个人独资企业投资者、合伙企业个人合伙人、承包承租经营者个人以及其他从事生产、经营活动的个人取得经营所得，应当依法缴纳个人所得税。"),
            createPolicy("关于个人所得税分类所得有关问题的公告", 1,
                "个人所得税分类所得包括利息、股息、红利所得，财产租赁所得，财产转让所得，偶然所得。分类所得适用比例税率，税率为百分之二十。"),
            createPolicy("关于个人所得税税率有关问题的公告", 1,
                "综合所得适用百分之三至百分之四十五的超额累进税率；经营所得适用百分之五至百分之三十五的超额累进税率；利息、股息、红利所得，财产租赁所得，财产转让所得和偶然所得，适用比例税率，税率为百分之二十。"),
            createPolicy("关于个人所得税应纳税所得额计算有关问题的公告", 1,
                "居民个人的综合所得，以每一纳税年度的收入额减除费用六万元以及专项扣除、专项附加扣除和依法确定的其他扣除后的余额，为应纳税所得额。"),
            createPolicy("关于个人所得税境外所得税收抵免有关问题的公告", 1,
                "居民个人从中国境外取得的所得，可以从其应纳税额中抵免已在境外缴纳的个人所得税税额，但抵免额不得超过该纳税人境外所得依照本法规定计算的应纳税额。"),
            createPolicy("关于个人所得税纳税期限有关问题的公告", 1,
                "居民个人取得综合所得，按年计算个人所得税；有扣缴义务人的，由扣缴义务人按月或者按次预扣预缴税款；需要办理汇算清缴的，应当在取得所得的次年三月一日至六月三十日内办理汇算清缴。"),
            createPolicy("关于个人所得税纳税地点有关问题的公告", 1,
                "居民个人取得综合所得，按年计算个人所得税；有扣缴义务人的，由扣缴义务人按月或者按次预扣预缴税款；需要办理汇算清缴的，应当在取得所得的次年三月一日至六月三十日内办理汇算清缴。")
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

    /**
     * 调用AI填充政策解读和计税规则
     */
    private void fillAiInfo(Policy policy) {
        try {
            String prompt = buildPrompt(policy.getContent());
            String result = aiService.call(prompt);
            // 统一清洗脏字符，保障JSON可解析
            result = clean(result);
            JSONObject json = JSON.parseObject(result);
            // 赋值入库
            policy.setAiSummary(json.getString("summary"));
            policy.setAiRules(json.getJSONArray("rules").toJSONString());
        } catch (Exception e) {
            // 异常兜底，避免页面报错、系统崩溃
            policy.setAiSummary("暂无AI解读");
            policy.setAiRules("[]");
        }
    }

    /**
     * 构建AI提示词
     */
    private String buildPrompt(String content) {
        return "你是专业个人所得税税务专家助手，严格遵守所有硬性格式约束，仅处理个税计税相关内容。\n" +
               "请根据提供的税务政策原文，固定完成两项任务，全程只返回纯净合法JSON，禁止任何多余文字、解释、备注、换行说明、Markdown、代码块、特殊符号。\n" +
               "任务1：提取个人所得税计算可用规则\n" +
               "- 仅保留可直接用于金额计算的有效内容：免征额、专项附加扣除、定额扣除、扣除标准、固定比例等\n" +
               "- 严格使用固定字段，不新增、不删减、不修改字段名：\n" +
               "  name：扣除/政策项目完整名称（中文）\n" +
               "  amount：纯数字数值，仅保留数字，禁止汉字、单位、符号、元/月等后缀\n" +
               "  unit：仅允许固定枚举值：per_month/per_year\n" +
               "- 无任何个税计税相关内容时，rules必须为空数组：[]\n" +
               "任务2：生成政策通俗解读\n" +
               "- 语言通俗易懂、简洁凝练\n" +
               "- 仅限说明该政策对个人所得税的实际影响\n" +
               "- 字数严格控制在100字以内\n" +
               "强制刚性规则：\n" +
               "1. 输出结果必须是标准、可直接反序列化的合法JSON\n" +
               "2. 禁止输出、json、注释、多余换行、前后多余字符\n" +
               "3. 禁止额外说明、额外描述、开场白、结束语\n" +
               "4. 严格遵循固定JSON结构输出，格式不可错乱\n" +
               "固定返回模板：\n" +
               "{\"rules\":[{\"name\":\"子女教育\",\"amount\":1000,\"unit\":\"per_month\"}],\"summary\":\"政策简要通俗解读内容\"}\n" +
               "政策内容如下：\n" +
               content;
    }

    /**
     * 清洗函数【优化增强版｜防各类特殊字符】
     */
    private String clean(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace("```json", "")
                .replace("```", "")
                .replace("\t", "")
                .replace("\r", "")
                .trim(); // 把 strip() 改成 trim()
    }
}