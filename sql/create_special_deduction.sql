-- 专项附加扣除表
CREATE TABLE IF NOT EXISTS `special_deduction` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `userId` bigint(20) NOT NULL COMMENT '关联用户id',
  `deductionType` int(11) NOT NULL COMMENT '扣除类型：1-子女教育 2-继续教育 3-大病医疗 4-住房贷款 5-住房租金 6-赡养老人',
  `amount` decimal(10,2) NOT NULL COMMENT '扣除金额',
  `startDate` datetime NOT NULL COMMENT '开始日期',
  `endDate` datetime NOT NULL COMMENT '结束日期',
  `status` int(11) NOT NULL COMMENT '扣除状态：0-未生效 1-生效中 2-已过期',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '更新时间',
  `isDelete` int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_deductionType` (`deductionType`),
  KEY `idx_status` (`status`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项附加扣除表';

-- 插入示例数据
INSERT INTO `special_deduction` (`id`, `userId`, `deductionType`, `amount`, `startDate`, `endDate`, `status`, `createTime`, `updateTime`, `isDelete`) VALUES
(1, 1, 1, 1000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 0),
(2, 1, 4, 1000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 0),
(3, 1, 6, 2000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, '2026-01-01 00:00:00', '2026-01-01 00:00:00', 0);
