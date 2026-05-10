package com.zihao.taxhelperai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zihao.taxhelperai.model.entity.TaxMessage;

import java.util.List;

/**
 * 税务消息服务接口
 */
public interface TaxMessageService {

    /**
     * 根据ID获取消息
     */
    TaxMessage getById(Long id);

    /**
     * 创建消息
     */
    TaxMessage createMessage(TaxMessage message);

    /**
     * 分页查询用户消息
     */
    IPage<TaxMessage> getUserMessages(Page<TaxMessage> page, Long userId, String messageType, Integer isRead);

    /**
     * 获取未读消息数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long userId, Long messageId);

    /**
     * 批量标记消息为已读
     */
    boolean batchMarkAsRead(Long userId, List<Long> messageIds);

    /**
     * 标记所有消息为已读
     */
    boolean markAllAsRead(Long userId);

    /**
     * 删除消息
     */
    boolean deleteMessage(Long userId, Long messageId);

    /**
     * 创建补税提醒消息
     */
    TaxMessage createTaxPayMessage(Long userId, String content);

    /**
     * 创建退税提醒消息
     */
    TaxMessage createTaxRefundMessage(Long userId, String content);

    /**
     * 创建风险提醒消息
     */
    TaxMessage createRiskMessage(Long userId, String content);

    /**
     * 创建AI建议消息
     */
    TaxMessage createAiAdviceMessage(Long userId, String content);

    /**
     * 创建系统通知消息
     */
    TaxMessage createSystemMessage(Long userId, String title, String content);
}