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


-- ---------------------------------------------------------------------------------
-- 最新正确的建表语句
create table if not exists ai_chat
(
    id        int auto_increment comment '主键'
        primary key,
    user_id   int                                not null comment '关联用户id',
    question  text                               not null comment '用户问题',
    answer    text                               not null comment 'AI回答',
    chat_time datetime default CURRENT_TIMESTAMP null comment '问答时间',
    isDelete  tinyint  default 0                 null comment '逻辑删除 0-未删除 1-已删除'
)
    comment 'AI问答记录表' charset = utf8mb4;

create index idx_is_delete
    on ai_chat (isDelete);

create index idx_user_id
    on ai_chat (user_id)
    comment '用户id索引';

create table if not exists guide
(
    id        int auto_increment comment '主键'
        primary key,
    policy_id int               not null comment '关联政策id',
    steps     text              not null comment '申报流程步骤(JSON)',
    tips      text              null comment '易错点提醒',
    materials text              null comment '材料清单',
    isDelete  tinyint default 0 null comment '逻辑删除 0-未删除 1-已删除'
)
    comment '申报指引表' charset = utf8mb4;

create index idx_is_delete
    on guide (isDelete);

create index idx_policy_id
    on guide (policy_id)
    comment '政策id索引';

create table if not exists policy
(
    id         int auto_increment comment '主键'
        primary key,
    title      varchar(50)              not null comment '政策标题',
    content    text                     not null comment '政策内容',
    type       tinyint                  not null comment '政策类型 1-基础政策 2-扣除标准 3-申报流程',
    createUser int                      not null comment '创建人(管理员id)',
    createTime datetime default (now()) null comment '创建时间',
    updateTime datetime default (now()) null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0       null comment '逻辑删除 0-未删除 1-已删除'
)
    comment '税务政策表' charset = utf8mb4;

create index idx_is_delete
    on policy (isDelete);

create table if not exists special_deduction
(
    id            bigint         not null comment '主键'
        primary key,
    userId        bigint         not null comment '关联用户id',
    deductionType int            not null comment '扣除类型：1-子女教育 2-继续教育 3-大病医疗 4-住房贷款 5-住房租金 6-赡养老人',
    amount        decimal(10, 2) not null comment '扣除金额',
    startDate     datetime       not null comment '开始日期',
    endDate       datetime       not null comment '结束日期',
    status        int            not null comment '扣除状态：0-未生效 1-生效中 2-已过期',
    createTime    datetime       not null comment '创建时间',
    updateTime    datetime       not null comment '更新时间',
    isDelete      int default 0  not null comment '逻辑删除 0-未删除 1-已删除'
)
    comment '专项附加扣除表';

create index idx_createTime
    on special_deduction (createTime);

create index idx_deductionType
    on special_deduction (deductionType);

create index idx_status
    on special_deduction (status);

create index idx_userId
    on special_deduction (userId);

create table if not exists system_log
(
    id        int auto_increment comment '主键'
        primary key,
    user_id   int                                   not null comment '操作用户id',
    operation varchar(50)                           not null comment '操作类型',
    ip        varchar(20) default ''                null comment '操作IP',
    oper_time datetime    default CURRENT_TIMESTAMP null comment '操作时间'
)
    comment '系统操作日志表' charset = utf8mb4;

create index idx_user_id
    on system_log (user_id)
    comment '用户id索引';

create table if not exists tax_record
(
    id        bigint auto_increment comment '主键'
        primary key,
    userId    bigint         default 0       not null comment '关联用户id',
    income    decimal(10, 2)                 not null comment '收入金额',
    insurance decimal(10, 2) default 0.00    null comment '五险一金',
    deduct    decimal(10, 2) default 0.00    null comment '专项附加扣除',
    taxAmount decimal(10, 2)                 not null comment '应缴税额',
    calcType  tinyint                        not null comment '计算类型 1-月薪 2-年度汇算',
    calcTime  datetime       default (now()) null comment '计算时间',
    isDelete  tinyint        default 0       null comment '逻辑删除 0-未删除 1-已删除'
)
    comment '计税记录表' charset = utf8mb4;

create index idx_is_delete
    on tax_record (isDelete);

create index idx_user_id
    on tax_record (userId)
    comment '用户id索引';

create table if not exists user
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    userAccount  varchar(20)                           not null comment '账号（手机号）',
    userPassword varchar(32)                           not null comment '加密密码（MD5）',
    realName     varchar(10) default ''                null comment '真实姓名',
    idCard       varchar(18) default ''                null comment '身份证号（脱敏）',
    taxRegion    varchar(50) default ''                null comment '税务所属地区（如：北京市海淀区）',
    userRole     varchar(10) default 'user'            null comment '用户角色 user-普通用户 admin-管理员',
    createTime   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint     default 0                 null comment '逻辑删除 0-未删除 1-已删除',
    constraint uk_user_account
        unique (userAccount)
)
    comment '用户表（基于AI的税务小助手）' charset = utf8mb4;

create index idx_is_delete
    on user (isDelete);

create index idx_tax_region
    on user (taxRegion);

create table if not exists verify_code
(
    id          int auto_increment comment '主键'
        primary key,
    phone       varchar(11) not null comment '手机号',
    code        varchar(6)  not null comment '验证码',
    expire_time datetime    not null comment '过期时间'
)
    comment '验证码表' charset = utf8mb4;

create index idx_phone
    on verify_code (phone)
    comment '手机号索引';

