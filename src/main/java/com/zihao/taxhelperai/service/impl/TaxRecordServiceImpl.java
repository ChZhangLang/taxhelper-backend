package com.zihao.taxhelperai.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.constant.CommonConstant;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.mapper.TaxRecordMapper;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordQueryRequest;
import com.zihao.taxhelperai.model.entity.TaxRecord;
//import com.zihao.taxhelperai.model.entity.TaxRecordFavour;
//import com.zihao.taxhelperai.model.entity.TaxRecordThumb;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;
import com.zihao.taxhelperai.model.vo.UserVO;
import com.zihao.taxhelperai.service.TaxRecordService;
import com.zihao.taxhelperai.service.UserService;
import com.zihao.taxhelperai.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 计税记录服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class TaxRecordServiceImpl extends ServiceImpl<TaxRecordMapper, TaxRecord> implements TaxRecordService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param taxRecord
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validTaxRecord(TaxRecord taxRecord, boolean add) {
        ThrowUtils.throwIf(taxRecord == null, ErrorCode.PARAMS_ERROR);
        // todo 不应该检验title
//        String title = taxRecord.getTitle();
//        // 创建数据时，参数不能为空
//        if (add) {
//            // todo 补充校验规则
//            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
//        }
//        // 修改数据时，有参数则校验
//        // todo 补充校验规则
//        if (StringUtils.isNotBlank(title)) {
//            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
//        }
    }

    /**
     * 获取查询条件
     *
     * @param taxRecordQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<TaxRecord> getQueryWrapper(TaxRecordQueryRequest taxRecordQueryRequest) {
        QueryWrapper<TaxRecord> queryWrapper = new QueryWrapper<>();
        if (taxRecordQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = taxRecordQueryRequest.getId();
        Long notId = taxRecordQueryRequest.getNotId();
        String title = taxRecordQueryRequest.getTitle();
        String content = taxRecordQueryRequest.getContent();
        String searchText = taxRecordQueryRequest.getSearchText();
        String sortField = taxRecordQueryRequest.getSortField();
        String sortOrder = taxRecordQueryRequest.getSortOrder();
        List<String> tagList = taxRecordQueryRequest.getTags();
        Long userId = taxRecordQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取计税记录封装
     *
     * @param taxRecord
     * @param request
     * @return
     */
    @Override
    public TaxRecordVO getTaxRecordVO(TaxRecord taxRecord, HttpServletRequest request) {
        // 对象转封装类
        TaxRecordVO taxRecordVO = TaxRecordVO.objToVo(taxRecord);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = taxRecord.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        taxRecordVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long taxRecordId = taxRecord.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        // todo 不应该获取点赞
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<TaxRecordThumb> taxRecordThumbQueryWrapper = new QueryWrapper<>();
//            taxRecordThumbQueryWrapper.in("taxRecordId", taxRecordId);
//            taxRecordThumbQueryWrapper.eq("userId", loginUser.getId());
//            TaxRecordThumb taxRecordThumb = taxRecordThumbMapper.selectOne(taxRecordThumbQueryWrapper);
//            taxRecordVO.setHasThumb(taxRecordThumb != null);
//            // 获取收藏
//            QueryWrapper<TaxRecordFavour> taxRecordFavourQueryWrapper = new QueryWrapper<>();
//            taxRecordFavourQueryWrapper.in("taxRecordId", taxRecordId);
//            taxRecordFavourQueryWrapper.eq("userId", loginUser.getId());
//            TaxRecordFavour taxRecordFavour = taxRecordFavourMapper.selectOne(taxRecordFavourQueryWrapper);
//            taxRecordVO.setHasFavour(taxRecordFavour != null);
//        }
        // endregion

        return taxRecordVO;
    }

    /**
     * 分页获取计税记录封装
     *
     * @param taxRecordPage
     * @param request
     * @return
     */
    @Override
    public Page<TaxRecordVO> getTaxRecordVOPage(Page<TaxRecord> taxRecordPage, HttpServletRequest request) {
        List<TaxRecord> taxRecordList = taxRecordPage.getRecords();
        Page<TaxRecordVO> taxRecordVOPage = new Page<>(taxRecordPage.getCurrent(), taxRecordPage.getSize(), taxRecordPage.getTotal());
        if (CollUtil.isEmpty(taxRecordList)) {
            return taxRecordVOPage;
        }
        // 对象列表 => 封装对象列表
        List<TaxRecordVO> taxRecordVOList = taxRecordList.stream().map(taxRecord -> {
            return TaxRecordVO.objToVo(taxRecord);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = taxRecordList.stream().map(TaxRecord::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> taxRecordIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> taxRecordIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        // todo 不应该获取点赞
//        if (loginUser != null) {
//            Set<Long> taxRecordIdSet = taxRecordList.stream().map(TaxRecord::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<TaxRecordThumb> taxRecordThumbQueryWrapper = new QueryWrapper<>();
//            taxRecordThumbQueryWrapper.in("taxRecordId", taxRecordIdSet);
//            taxRecordThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<TaxRecordThumb> taxRecordTaxRecordThumbList = taxRecordThumbMapper.selectList(taxRecordThumbQueryWrapper);
//            taxRecordTaxRecordThumbList.forEach(taxRecordTaxRecordThumb -> taxRecordIdHasThumbMap.put(taxRecordTaxRecordThumb.getTaxRecordId(), true));
//            // 获取收藏
//            QueryWrapper<TaxRecordFavour> taxRecordFavourQueryWrapper = new QueryWrapper<>();
//            taxRecordFavourQueryWrapper.in("taxRecordId", taxRecordIdSet);
//            taxRecordFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<TaxRecordFavour> taxRecordFavourList = taxRecordFavourMapper.selectList(taxRecordFavourQueryWrapper);
//            taxRecordFavourList.forEach(taxRecordFavour -> taxRecordIdHasFavourMap.put(taxRecordFavour.getTaxRecordId(), true));
//        }
        // 填充信息
        taxRecordVOList.forEach(taxRecordVO -> {
            Long userId = taxRecordVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            taxRecordVO.setUser(userService.getUserVO(user));
            // todo 不应该获取点赞
//            taxRecordVO.setHasThumb(taxRecordIdHasThumbMap.getOrDefault(taxRecordVO.getId(), false));
//            taxRecordVO.setHasFavour(taxRecordIdHasFavourMap.getOrDefault(taxRecordVO.getId(), false));
        });
        // endregion

        taxRecordVOPage.setRecords(taxRecordVOList);
        return taxRecordVOPage;
    }

}
