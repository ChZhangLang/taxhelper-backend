-- 家属信息表
CREATE TABLE IF NOT EXISTS family_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    userId BIGINT NOT NULL COMMENT '关联用户id',
    relationship VARCHAR(20) NOT NULL COMMENT '与用户的关系（如：配偶、子女、父母等）',
    idCardType VARCHAR(20) DEFAULT '居民身份证' COMMENT '证件类型',
    idCard VARCHAR(18) NOT NULL COMMENT '证件号码',
    name VARCHAR(50) NOT NULL COMMENT '家属姓名',
    nationality VARCHAR(50) DEFAULT '中华人民共和国' COMMENT '国籍(地区)',
    birthDate DATE COMMENT '出生日期（根据证件号自动填入）',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    KEY idx_userId (userId),
    KEY idx_isDelete (isDelete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家属信息表';

-- 银行卡信息表
CREATE TABLE IF NOT EXISTS bank_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    userId BIGINT NOT NULL COMMENT '关联用户id',
    bankCardNo VARCHAR(20) NOT NULL COMMENT '银行卡号',
    bankName VARCHAR(50) NOT NULL COMMENT '所属银行',
    province VARCHAR(50) COMMENT '开户银行所在省份',
    phone VARCHAR(11) COMMENT '银行预留手机号码',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    KEY idx_userId (userId),
    KEY idx_isDelete (isDelete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='银行卡信息表';