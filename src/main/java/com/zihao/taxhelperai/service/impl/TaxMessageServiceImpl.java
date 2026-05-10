package com.zihao.taxhelperai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihao.taxhelperai.mapper.TaxMessageMapper;
import com.zihao.taxhelperai.model.entity.TaxMessage;
import com.zihao.taxhelperai.service.TaxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 税务消息服务实现类
 */
@Service
@Slf4j
public class TaxMessageServiceImpl extends ServiceImpl<TaxMessageMapper, TaxMessage> implements TaxMessageService {

    @Override
    public TaxMessage getById(Long id) {
        return super.getById(id);
    }

    @Override
    public TaxMessage createMessage(TaxMessage message) {
        message.setIsRead(0);
        message.setIsDelete(0);
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        if (save(message)) {
            log.info("创建消息成功: userId={}, type={}", message.getUserId(), message.getMessageType());
            return message;
        }
        return null;
    }

    @Override
    public IPage<TaxMessage> getUserMessages(Page<TaxMessage> page, Long userId, String messageType, Integer isRead) {
        return baseMapper.selectUserMessages(page, userId, messageType, isRead);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return baseMapper.countUnreadMessages(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long userId, Long messageId) {
        TaxMessage message = getById(messageId);
        if (message != null && message.getUserId().equals(userId)) {
            message.setIsRead(1);
            return updateById(message);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkAsRead(Long userId, List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return true;
        }
        return baseMapper.batchUpdateReadStatus(userId, messageIds) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        return baseMapper.markAllAsRead(userId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessage(Long userId, Long messageId) {
        TaxMessage message = getById(messageId);
        if (message != null && message.getUserId().equals(userId)) {
            return removeById(messageId);
        }
        return false;
    }

    @Override
    public TaxMessage createTaxPayMessage(Long userId, String content) {
        TaxMessage message = new TaxMessage();
        message.setUserId(userId);
        message.setTitle("补税提醒");
        message.setContent(content);
        message.setMessageType(TaxMessage.MessageType.TAX_PAY.name());
        message.setMessageLevel(TaxMessage.MessageLevel.HIGH.name());
        return createMessage(message);
    }

    @Override
    public TaxMessage createTaxRefundMessage(Long userId, String content) {
        TaxMessage message = new TaxMessage();
        message.setUserId(userId);
        message.setTitle("退税提醒");
        message.setContent(content);
        message.setMessageType(TaxMessage.MessageType.TAX_REFUND.name());
        message.setMessageLevel(TaxMessage.MessageLevel.MEDIUM.name());
        return createMessage(message);
    }

    @Override
    public TaxMessage createRiskMessage(Long userId, String content) {
        TaxMessage message = new TaxMessage();
        message.setUserId(userId);
        message.setTitle("风险预警");
        message.setContent(content);
        message.setMessageType(TaxMessage.MessageType.RISK.name());
        message.setMessageLevel(TaxMessage.MessageLevel.HIGH.name());
        return createMessage(message);
    }

    @Override
    public TaxMessage createAiAdviceMessage(Long userId, String content) {
        TaxMessage message = new TaxMessage();
        message.setUserId(userId);
        message.setTitle("AI税务建议");
        message.setContent(content);
        message.setMessageType(TaxMessage.MessageType.AI_ADVICE.name());
        message.setMessageLevel(TaxMessage.MessageLevel.MEDIUM.name());
        return createMessage(message);
    }

    @Override
    public TaxMessage createSystemMessage(Long userId, String title, String content) {
        TaxMessage message = new TaxMessage();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setMessageType(TaxMessage.MessageType.SYSTEM.name());
        message.setMessageLevel(TaxMessage.MessageLevel.LOW.name());
        return createMessage(message);
    }
}