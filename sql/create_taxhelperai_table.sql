-- 创建数据库
CREATE DATABASE IF NOT EXISTS tax_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tax_assistant;

-- 1. 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
                        id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户主键',
                        username VARCHAR(20) NOT NULL COMMENT '账号(手机号)',
                        password VARCHAR(32) NOT NULL COMMENT '加密密码',
                        real_name VARCHAR(10) DEFAULT '' COMMENT '真实姓名',
                        id_card VARCHAR(18) DEFAULT '' COMMENT '身份证号(脱敏)',
                        phone VARCHAR(11) NOT NULL COMMENT '手机号',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        is_admin TINYINT DEFAULT 0 COMMENT '是否管理员 0-否 1-是',
                        UNIQUE KEY uk_username (username) COMMENT '账号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户信息扩展表
DROP TABLE IF EXISTS t_user_info;
CREATE TABLE t_user_info (
                             id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                             user_id INT NOT NULL COMMENT '关联用户id',
                             address VARCHAR(100) DEFAULT '' COMMENT '常用地址',
                             email VARCHAR(50) DEFAULT '' COMMENT '邮箱',
                             update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             KEY idx_user_id (user_id) COMMENT '用户id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息扩展表';

-- 3. 计税记录表
DROP TABLE IF EXISTS t_tax_record;
CREATE TABLE t_tax_record (
                              id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                              user_id INT NOT NULL COMMENT '关联用户id',
                              income DECIMAL(10,2) NOT NULL COMMENT '收入金额',
                              insurance DECIMAL(10,2) DEFAULT 0.00 COMMENT '五险一金',
                              deduct DECIMAL(10,2) DEFAULT 0.00 COMMENT '专项附加扣除',
                              tax_amount DECIMAL(10,2) NOT NULL COMMENT '应缴税额',
                              calc_type TINYINT NOT NULL COMMENT '计算类型 1-月薪 2-年度汇算',
                              calc_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
                              KEY idx_user_id (user_id) COMMENT '用户id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计税记录表';

-- 4. 税务政策表
DROP TABLE IF EXISTS t_policy;
CREATE TABLE t_policy (
                          id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                          title VARCHAR(50) NOT NULL COMMENT '政策标题',
                          content TEXT NOT NULL COMMENT '政策内容',
                          type TINYINT NOT NULL COMMENT '政策类型 1-基础政策 2-扣除标准 3-申报流程',
                          create_user INT NOT NULL COMMENT '创建人(管理员id)',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='税务政策表';

-- 5. 申报指引表（已修复）
DROP TABLE IF EXISTS t_guide;
CREATE TABLE t_guide (
                         id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                         policy_id INT NOT NULL COMMENT '关联政策id',
                         steps TEXT NOT NULL COMMENT '申报流程步骤(JSON)',
                         tips TEXT COMMENT '易错点提醒',
                         materials TEXT COMMENT '材料清单',
                         KEY idx_policy_id (policy_id) COMMENT '政策id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申报指引表';


-- 6. AI问答记录表
DROP TABLE IF EXISTS t_ai_chat;
CREATE TABLE t_ai_chat (
                           id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                           user_id INT NOT NULL COMMENT '关联用户id',
                           question TEXT NOT NULL COMMENT '用户问题',
                           answer TEXT NOT NULL COMMENT 'AI回答',
                           chat_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '问答时间',
                           KEY idx_user_id (user_id) COMMENT '用户id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问答记录表';

-- 7. 系统操作日志表
DROP TABLE IF EXISTS t_system_log;
CREATE TABLE t_system_log (
                              id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                              user_id INT NOT NULL COMMENT '操作用户id',
                              operation VARCHAR(50) NOT NULL COMMENT '操作类型',
                              ip VARCHAR(20) DEFAULT '' COMMENT '操作IP',
                              oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                              KEY idx_user_id (user_id) COMMENT '用户id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- 8. 验证码表
DROP TABLE IF EXISTS t_verify_code;
CREATE TABLE t_verify_code (
                               id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                               phone VARCHAR(11) NOT NULL COMMENT '手机号',
                               code VARCHAR(6) NOT NULL COMMENT '验证码',
                               expire_time DATETIME NOT NULL COMMENT '过期时间',
                               KEY idx_phone (phone) COMMENT '手机号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 初始化管理员账号（账号：19856880328，密码：123456，MD5加密后：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO t_user (username, password, phone, is_admin)
VALUES ('19856880328', 'e10adc3949ba59abbe56e057f20f883e', '19856880328', 1);

-- 初始化测试政策数据（适配中期答辩演示）
INSERT INTO t_policy (title, content, type, create_user)
VALUES (
           '租房专项附加扣除标准',
           '一、政策原文：直辖市、省会（首府）城市、计划单列市以及国务院确定的其他城市，扣除标准为每月1500元；市辖区户籍人口超过100万的城市，扣除标准为每月1100元；市辖区户籍人口不超过100万的城市，扣除标准为每月800元。二、通俗解读：租房扣除金额按城市规模分三档，不用提供租房发票，仅需填写租房信息即可申报。',
           2,
           1
       );