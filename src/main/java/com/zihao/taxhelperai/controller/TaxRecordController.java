package com.zihao.taxhelperai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zihao.taxhelperai.annotation.AuthCheck;
import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.DeleteRequest;
import com.zihao.taxhelperai.common.ErrorCode;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.constant.UserConstant;
import com.zihao.taxhelperai.exception.BusinessException;
import com.zihao.taxhelperai.exception.ThrowUtils;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordAddRequest;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordEditRequest;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordQueryRequest;
import com.zihao.taxhelperai.model.dto.taxRecord.TaxRecordUpdateRequest;
import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.TaxRecordVO;
import com.zihao.taxhelperai.service.TaxRecordService;
import com.zihao.taxhelperai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 计税记录接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/taxRecord")
@Slf4j
public class TaxRecordController {

    @Resource
    private TaxRecordService taxRecordService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建计税记录
     *
     * @param taxRecordAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTaxRecord(@RequestBody TaxRecordAddRequest taxRecordAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(taxRecordAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        TaxRecord taxRecord = new TaxRecord();
        BeanUtils.copyProperties(taxRecordAddRequest, taxRecord);
        // 数据校验
        taxRecordService.validTaxRecord(taxRecord, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        taxRecord.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = taxRecordService.save(taxRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newTaxRecordId = taxRecord.getId();
        return ResultUtils.success(newTaxRecordId);
    }

    /**
     * 删除计税记录
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTaxRecord(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        TaxRecord oldTaxRecord = taxRecordService.getById(id);
        ThrowUtils.throwIf(oldTaxRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldTaxRecord.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = taxRecordService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新计税记录（仅管理员可用）
     *
     * @param taxRecordUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTaxRecord(@RequestBody TaxRecordUpdateRequest taxRecordUpdateRequest) {
        if (taxRecordUpdateRequest == null || taxRecordUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        TaxRecord taxRecord = new TaxRecord();
        BeanUtils.copyProperties(taxRecordUpdateRequest, taxRecord);
        // 数据校验
        taxRecordService.validTaxRecord(taxRecord, false);
        // 判断是否存在
        long id = taxRecordUpdateRequest.getId();
        TaxRecord oldTaxRecord = taxRecordService.getById(id);
        ThrowUtils.throwIf(oldTaxRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = taxRecordService.updateById(taxRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取计税记录（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<TaxRecordVO> getTaxRecordVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        TaxRecord taxRecord = taxRecordService.getById(id);
        ThrowUtils.throwIf(taxRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(taxRecordService.getTaxRecordVO(taxRecord, request));
    }

    /**
     * 分页获取计税记录列表（仅管理员可用）
     *
     * @param taxRecordQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<TaxRecord>> listTaxRecordByPage(@RequestBody TaxRecordQueryRequest taxRecordQueryRequest) {
        long current = taxRecordQueryRequest.getCurrent();
        long size = taxRecordQueryRequest.getPageSize();
        // 查询数据库
        Page<TaxRecord> taxRecordPage = taxRecordService.page(new Page<>(current, size),
                taxRecordService.getQueryWrapper(taxRecordQueryRequest));
        return ResultUtils.success(taxRecordPage);
    }

    /**
     * 分页获取计税记录列表（封装类）
     *
     * @param taxRecordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<TaxRecordVO>> listTaxRecordVOByPage(@RequestBody TaxRecordQueryRequest taxRecordQueryRequest,
                                                               HttpServletRequest request) {
        long current = taxRecordQueryRequest.getCurrent();
        long size = taxRecordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TaxRecord> taxRecordPage = taxRecordService.page(new Page<>(current, size),
                taxRecordService.getQueryWrapper(taxRecordQueryRequest));
        // 获取封装类
        return ResultUtils.success(taxRecordService.getTaxRecordVOPage(taxRecordPage, request));
    }

    /**
     * 分页获取当前登录用户创建的计税记录列表
     *
     * @param taxRecordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<TaxRecordVO>> listMyTaxRecordVOByPage(@RequestBody TaxRecordQueryRequest taxRecordQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(taxRecordQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        taxRecordQueryRequest.setUserId(loginUser.getId());
        long current = taxRecordQueryRequest.getCurrent();
        long size = taxRecordQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<TaxRecord> taxRecordPage = taxRecordService.page(new Page<>(current, size),
                taxRecordService.getQueryWrapper(taxRecordQueryRequest));
        // 获取封装类
        return ResultUtils.success(taxRecordService.getTaxRecordVOPage(taxRecordPage, request));
    }

    /**
     * 编辑计税记录（给用户使用）
     *
     * @param taxRecordEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editTaxRecord(@RequestBody TaxRecordEditRequest taxRecordEditRequest, HttpServletRequest request) {
        if (taxRecordEditRequest == null || taxRecordEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        TaxRecord taxRecord = new TaxRecord();
        BeanUtils.copyProperties(taxRecordEditRequest, taxRecord);
        // 数据校验
        taxRecordService.validTaxRecord(taxRecord, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = taxRecordEditRequest.getId();
        TaxRecord oldTaxRecord = taxRecordService.getById(id);
        ThrowUtils.throwIf(oldTaxRecord == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldTaxRecord.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = taxRecordService.updateById(taxRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
