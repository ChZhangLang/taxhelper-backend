# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists tax_assistent;

-- 切换库
use tax_assistent;

-- 切换到税务助手数据库
USE tax_assistant;

-- 先删除原有user表（若存在，谨慎执行！执行前可备份数据）
DROP TABLE IF EXISTS user;

-- 创建最终版user表
CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                      userAccount VARCHAR(20) NOT NULL COMMENT '账号（手机号）',
                      userPassword VARCHAR(32) NOT NULL COMMENT '加密密码（MD5）',
                      realName VARCHAR(10) DEFAULT '' COMMENT '真实姓名',
                      idCard VARCHAR(18) DEFAULT '' COMMENT '身份证号（脱敏）',
                      taxRegion VARCHAR(50) DEFAULT '' COMMENT '税务所属地区（如：北京市海淀区）',
                      userRole VARCHAR(10) DEFAULT 'user' COMMENT '用户角色 user-普通用户 admin-管理员',
                      createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      isDelete TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    -- 唯一索引：防止同一手机号重复注册
                      UNIQUE KEY uk_user_account (userAccount),
    -- 普通索引：提升逻辑删除查询效率
                      KEY idx_is_delete (isDelete),
    -- 普通索引：提升按税务地区查询用户的效率（适配管理员统计）
                      KEY idx_tax_region (taxRegion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表（基于AI的税务小助手）';

-- 初始化管理员账号（便于调试）
-- 账号：19856880328，密码：123456（MD5加密后：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO user (userAccount, userPassword, userRole)
VALUES ('19856880328', 'e10adc3949ba59abbe56e057f20f883e', 'admin');

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';
