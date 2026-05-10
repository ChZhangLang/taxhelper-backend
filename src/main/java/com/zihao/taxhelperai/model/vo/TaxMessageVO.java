package com.zihao.taxhelperai.model.vo;

import com.zihao.taxhelperai.model.entity.TaxMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 税务消息VO
 */
@Data
public class TaxMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息类型描述
     */
    private String messageTypeDesc;

    /**
     * 消息等级
     */
    private String messageLevel;

    /**
     * 消息等级描述
     */
    private String messageLevelDesc;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 从实体转换
     */
    public static TaxMessageVO fromEntity(TaxMessage entity) {
        if (entity == null) {
            return null;
        }
        TaxMessageVO vo = new TaxMessageVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setMessageType(entity.getMessageType());
        vo.setMessageLevel(entity.getMessageLevel());
        vo.setIsRead(entity.getIsRead() != null && entity.getIsRead() == 1);
        vo.setCreateTime(entity.getCreateTime());

        // 设置类型描述
        try {
            if (entity.getMessageType() != null) {
                TaxMessage.MessageType type = TaxMessage.MessageType.valueOf(entity.getMessageType());
                vo.setMessageTypeDesc(type.getDescription());
            } else {
                vo.setMessageTypeDesc("未知类型");
            }
        } catch (IllegalArgumentException e) {
            vo.setMessageTypeDesc(entity.getMessageType());
        }

        // 设置等级描述
        try {
            if (entity.getMessageLevel() != null) {
                TaxMessage.MessageLevel level = TaxMessage.MessageLevel.valueOf(entity.getMessageLevel());
                vo.setMessageLevelDesc(level.getDescription());
            } else {
                vo.setMessageLevelDesc("未知等级");
            }
        } catch (IllegalArgumentException e) {
            vo.setMessageLevelDesc(entity.getMessageLevel());
        }

        return vo;
    }
}