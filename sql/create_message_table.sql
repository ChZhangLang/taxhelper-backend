-- 创建消息表
USE tax_assistant;

DROP TABLE IF EXISTS tax_message;

CREATE TABLE tax_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    title VARCHAR(100) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型：TAX_PAY-补税提醒，TAX_REFUND-退税提醒，RISK-风险提醒，AI_ADVICE-AI建议，SYSTEM-系统通知',
    message_level VARCHAR(10) NOT NULL COMMENT '消息等级：HIGH-高，MEDIUM-中，LOW-低',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    KEY idx_user_id (user_id),
    KEY idx_is_read (is_read),
    KEY idx_message_type (message_type),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='税务消息表';
