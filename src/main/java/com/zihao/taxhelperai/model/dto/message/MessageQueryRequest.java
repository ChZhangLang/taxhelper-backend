package com.zihao.taxhelperai.model.dto.message;

import lombok.Data;

/**
 * 消息查询请求
 */
@Data
public class MessageQueryRequest {

    /**
     * 消息类型筛选：TAX_PAY, TAX_REFUND, RISK, AI_ADVICE, SYSTEM
     */
    private String messageType;

    /**
     * 是否已读筛选：0-未读，1-已读，null-全部
     */
    private Integer isRead;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 20;
}