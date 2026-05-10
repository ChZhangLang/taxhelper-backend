package com.zihao.taxhelperai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 税务消息实体
 */
@Data
@TableName("tax_message")
public class TaxMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 消息标题
     */
    @TableField("title")
    private String title;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 消息等级
     */
    @TableField("message_level")
    private String messageLevel;

    /**
     * 是否已读：0-未读，1-已读
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TAX_PAY("补税提醒"),
        TAX_REFUND("退税提醒"),
        RISK("风险提醒"),
        AI_ADVICE("AI建议"),
        SYSTEM("系统通知");

        private final String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 消息等级枚举
     */
    public enum MessageLevel {
        HIGH("高"),
        MEDIUM("中"),
        LOW("低");

        private final String description;

        MessageLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}