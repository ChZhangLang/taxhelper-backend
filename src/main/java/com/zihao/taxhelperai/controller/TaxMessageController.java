package com.zihao.taxhelperai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zihao.taxhelperai.common.BaseResponse;
import com.zihao.taxhelperai.common.ResultUtils;
import com.zihao.taxhelperai.model.dto.message.MessageQueryRequest;
import com.zihao.taxhelperai.model.entity.TaxMessage;
import com.zihao.taxhelperai.model.entity.User;
import com.zihao.taxhelperai.model.vo.TaxMessageVO;
import com.zihao.taxhelperai.service.TaxMessageService;
import com.zihao.taxhelperai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 税务消息控制器
 */
@RestController
@RequestMapping("/message")
@Slf4j
public class TaxMessageController {

    @Autowired
    private TaxMessageService taxMessageService;

    @Autowired
    private UserService userService;

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public BaseResponse<IPage<TaxMessageVO>> getMessageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) Integer isRead,
            HttpServletRequest request) {

        User user = userService.getLoginUser(request);
        Page<TaxMessage> page = new Page<>(pageNum, pageSize);
        IPage<TaxMessage> messagePage = taxMessageService.getUserMessages(page, user.getId(), messageType, isRead);

        IPage<TaxMessageVO> voPage = messagePage.convert(TaxMessageVO::fromEntity);
        return ResultUtils.success(voPage);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public BaseResponse<Map<String, Object>> getUnreadCount(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        Long count = taxMessageService.getUnreadCount(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", count);
        return ResultUtils.success(result);
    }

    /**
     * 标记单条消息已读
     */
    @PostMapping("/read/{messageId}")
    public BaseResponse<Boolean> markAsRead(@PathVariable Long messageId, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        boolean success = taxMessageService.markAsRead(user.getId(), messageId);
        return ResultUtils.success(success);
    }

    /**
     * 标记所有消息已读
     */
    @PostMapping("/read/all")
    public BaseResponse<Boolean> markAllAsRead(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        boolean success = taxMessageService.markAllAsRead(user.getId());
        return ResultUtils.success(success);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public BaseResponse<Boolean> deleteMessage(@PathVariable Long messageId, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        boolean success = taxMessageService.deleteMessage(user.getId(), messageId);
        return ResultUtils.success(success);
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    public BaseResponse<TaxMessageVO> getMessageDetail(@PathVariable Long messageId, HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        TaxMessage message = taxMessageService.getById(messageId);

        if (message == null || !message.getUserId().equals(user.getId())) {
            return ResultUtils.error(404, "消息不存在");
        }

        // 自动标记为已读
        taxMessageService.markAsRead(user.getId(), messageId);

        return ResultUtils.success(TaxMessageVO.fromEntity(message));
    }
}