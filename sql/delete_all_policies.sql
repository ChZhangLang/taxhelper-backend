-- ==============================================
-- 删除所有税务政策数据（执行此脚本前请备份数据）
-- ==============================================

-- 切换到数据库
USE tax_assistant;

-- 关闭外键约束（如果有外键关联）
SET FOREIGN_KEY_CHECKS = 0;

-- 删除所有政策数据
DELETE FROM policy WHERE 1=1;

-- 删除关联的申报指引数据
DELETE FROM guide WHERE 1=1;

-- 重置自增主键
ALTER TABLE policy AUTO_INCREMENT = 1;
ALTER TABLE guide AUTO_INCREMENT = 1;

-- 恢复外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 验证删除结果
SELECT COUNT(*) AS policy_count FROM policy;
SELECT COUNT(*) AS guide_count FROM guide;

-- ==============================================
-- 执行完成后，运行同步政策接口即可导入新政策数据
-- ==============================================