-- ================== 专项附加扣除模块重构 - 新表结构 ==================
-- 说明：将原有的单表special_deduction拆分为主档表+类型详情表结构

-- ================== 1. 专项附加扣除主档表 ==================
DROP TABLE IF EXISTS `tax_special_deduct`;
CREATE TABLE `tax_special_deduct` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `deduct_type` int(11) NOT NULL COMMENT '扣除类型：1-子女教育 2-继续教育 3-大病医疗 4-住房贷款 5-住房租金 6-赡养老人',
  `start_date` date NOT NULL COMMENT '生效开始日期',
  `end_date` date NOT NULL COMMENT '生效结束日期',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态：0-未生效 1-生效中 2-已过期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_deduct_type` (`deduct_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专项附加扣除主档表';

-- ================== 2. 子女教育详情表 ==================
DROP TABLE IF EXISTS `deduct_child_education`;
CREATE TABLE `deduct_child_education` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `deduct_id` bigint(20) NOT NULL COMMENT '关联主档ID',
  `child_name` varchar(50) NOT NULL COMMENT '子女姓名',
  `child_id_card` varchar(18) COMMENT '子女身份证号',
  `education_stage` varchar(50) COMMENT '教育阶段：学前教育/义务教育/高中/高等教育',
  `school_name` varchar(100) COMMENT '学校名称',
  `is_shared` tinyint(1) DEFAULT 0 COMMENT '是否分摊：0-否 1-是',
  `shared_ratio` decimal(5,2) DEFAULT 100.00 COMMENT '分摊比例%',
  `monthly_amount` decimal(10,2) NOT NULL COMMENT '月扣除金额（系统计算）',
  PRIMARY KEY (`id`),
  KEY `idx_deduct_id` (`deduct_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='子女教育详情表';

-- ================== 3. 继续教育详情表 ==================
DROP TABLE IF EXISTS `deduct_continue_education`;
CREATE TABLE `deduct_continue_education` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `deduct_id` bigint(20) NOT NULL COMMENT '关联主档ID',
  `education_type` int(11) NOT NULL COMMENT '教育类型：1-学历教育 2-职业资格教育',
  `education_name` varchar(100) COMMENT '教育名称/证书名称',
  `institution_name` varchar(100) COMMENT '学校/培训机构名称',
  `certificate_no` varchar(50) COMMENT '证书编号',
  `monthly_amount` decimal(10,2) NOT NULL COMMENT '月扣除金额（系统计算）',
  PRIMARY KEY (`id`),
  KEY `idx_deduct_id` (`deduct_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='继续教育详情表';

-- ================== 4. 大病医疗详情表 ==================
DROP TABLE IF EXISTS `deduct_serious_illness`;
CREATE TABLE `deduct_serious_illness` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `deduct_id` bigint(20) NOT NULL COMMENT '关联主档ID',
  `patient_name` varchar(50) NOT NULL COMMENT '患者姓名',
  `patient_relation` int(11) NOT NULL COMMENT '与纳税人关系：1-本人 2-配偶 3-子女',
  `total_medical_expense` decimal(12,2) NOT NULL COMMENT '总医疗费用',
  `insurance_reimburse` decimal(12,2) DEFAULT 0 COMMENT '医保报销金额',
  `self_pay_expense` decimal(12,2) NOT NULL COMMENT '个人自费金额',
  `deductible_amount` decimal(12,2) COMMENT '可扣除金额（系统计算）',
  `year` int(11) NOT NULL COMMENT '费用发生年份',
  PRIMARY KEY (`id`),
  KEY `idx_deduct_id` (`deduct_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大病医疗详情表';

-- ================== 5. 住房贷款利息详情表 ==================
DROP TABLE IF EXISTS `deduct_house_loan`;
CREATE TABLE `deduct_house_loan` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `deduct_id` bigint(20) NOT NULL COMMENT '关联主档ID',
  `house_address` varchar(200) COMMENT '房屋地址',
  `is_first_house` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否首套：0-否 1-是',
  `loan_bank` varchar(100) COMMENT '贷款银行',
  `loan_start_date` date NOT NULL COMMENT '贷款开始日期',
  `total_months` int(11) DEFAULT 240 COMMENT '贷款总期限（月）',
  `used_months` int(11) DEFAULT 0 COMMENT '已使用期限（月）',
  `monthly_amount` decimal(10,2) NOT NULL DEFAULT 1000.00 COMMENT '月扣除金额（固定1000）',
  PRIMARY KEY (`id`),
  KEY `idx_deduct_id` (`deduct_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住房贷款利息详情表';

-- ================== 6. 住房租金详情表 ==================
DROP TABLE IF EXISTS `deduct_house_rent`;
CREATE TABLE `deduct_house_rent` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `deduct_id` bigint(20) NOT NULL COMMENT '关联主档ID',
  `rent_address` varchar(200) NOT NULL COMMENT '租赁房屋地址',
  `city_level` int(11) NOT NULL COMMENT '城市等级：1-直辖市/省会 2-中等城市 3-小城市',
  `has_house_in_city` tinyint(1) DEFAULT 0 COMMENT '本地是否有房：0-无 1-有',
  `monthly_rent` decimal(10,2) COMMENT '月租金（参考）',
  `monthly_amount` decimal(10,2) NOT NULL COMMENT '月扣除金额（系统计算）',
  PRIMARY KEY (`id`),
  KEY `idx_deduct_id` (`deduct_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住房租金详情表';

-- ================== 7. 赡养老人详情表 ==================
DROP TABLE IF EXISTS `deduct_elder_support`;
CREATE TABLE `deduct_elder_support` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `deduct_id` bigint(20) NOT NULL COMMENT '关联主档ID',
  `elder_name` varchar(50) NOT NULL COMMENT '老人姓名',
  `elder_id_card` varchar(18) COMMENT '老人身份证号',
  `elder_age` int(11) COMMENT '老人年龄（需≥60）',
  `is_only_child` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否独生子女：0-否 1-是',
  `shared_count` int(11) DEFAULT 1 COMMENT '分摊人数',
  `shared_ratio` decimal(5,2) COMMENT '分摊比例%',
  `monthly_amount` decimal(10,2) NOT NULL COMMENT '月扣除金额（系统计算）',
  PRIMARY KEY (`id`),
  KEY `idx_deduct_id` (`deduct_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赡养老人详情表';

-- ================== 数据迁移示例（可选） ==================
-- INSERT INTO tax_special_deduct(id, user_id, deduct_type, start_date, end_date, status, create_time, update_time)
-- SELECT id, userId, deductionType, startDate, endDate, status, createTime, updateTime 
-- FROM special_deduction WHERE isDelete = 0;
