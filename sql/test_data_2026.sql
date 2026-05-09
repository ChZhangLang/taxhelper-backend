-- ================================================
-- 2026年测试用户计税数据
-- 三个用户具有明显区分度，用于测试数据分析页面
-- ================================================
USE tax_assistant;

-- ================================================
-- 1. 创建测试用户（如果不存在）
-- ================================================
-- 用户1：虚伪陈 - 高收入人群
INSERT INTO user (user_account, user_password, real_name, id_card, tax_region, user_role, is_delete)
SELECT '13800138001', 'e10adc3949ba59abbe56e057f20f883e', '虚伪陈', '11010119900101****', '北京市朝阳区', 'user', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_account = '13800138001');

-- 用户2：李浩宇 - 中等收入人群
INSERT INTO user (user_account, user_password, real_name, id_card, tax_region, user_role, is_delete)
SELECT '13800138002', 'e10adc3949ba59abbe56e057f20f883e', '李浩宇', '31010119950505****', '上海市浦东新区', 'user', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_account = '13800138002');

-- 用户3：丁真 - 低收入人群
INSERT INTO user (user_account, user_password, real_name, id_card, tax_region, user_role, is_delete)
SELECT '13800138003', 'e10adc3949ba59abbe56e057f20f883e', '丁真', '51010120001212****', '四川省成都市', 'user', 0
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_account = '13800138003');

-- ================================================
-- 2. 获取用户ID变量
-- ================================================
SET @user_id_chen = (SELECT id FROM user WHERE user_account = '13800138001');
SET @user_id_li = (SELECT id FROM user WHERE user_account = '13800138002');
SET @user_id_ding = (SELECT id FROM user WHERE user_account = '13800138003');

-- ================================================
-- 3. 用户1：虚伪陈 - 高收入测试数据
-- 月薪：35000元，五险一金：5250元，专项附加扣除：4000元（子女教育1000+房贷1500+赡养老人1500）
-- ================================================

-- 专项附加扣除记录
INSERT INTO tax_special_deduct (user_id, deduct_type, status, is_delete)
VALUES 
(@user_id_chen, 1, 1, 0),  -- 子女教育
(@user_id_chen, 4, 1, 0),  -- 住房贷款利息
(@user_id_chen, 6, 1, 0);  -- 赡养老人

-- 月度预缴数据（1-12月）
INSERT INTO tax_record (user_id, income, insurance, deduct, tax_amount, calc_type, tax_year, tax_month, 
                       cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, 
                       taxable_income, before_tax_income, is_delete)
VALUES
-- 1月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 1425.00, 1, 2026, 1, 35000.00, 5250.00, 4000.00, 1425.00, 25750.00, 29750.00, 0),
-- 2月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 2625.00, 1, 2026, 2, 70000.00, 10500.00, 8000.00, 4050.00, 51500.00, 29750.00, 0),
-- 3月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 2625.00, 1, 2026, 3, 105000.00, 15750.00, 12000.00, 6675.00, 77250.00, 29750.00, 0),
-- 4月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 3825.00, 1, 2026, 4, 140000.00, 21000.00, 16000.00, 10500.00, 103000.00, 29750.00, 0),
-- 5月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 3825.00, 1, 2026, 5, 175000.00, 26250.00, 20000.00, 14325.00, 128750.00, 29750.00, 0),
-- 6月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 3825.00, 1, 2026, 6, 210000.00, 31500.00, 24000.00, 18150.00, 154500.00, 29750.00, 0),
-- 7月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 4375.00, 1, 2026, 7, 245000.00, 36750.00, 28000.00, 22525.00, 180250.00, 29750.00, 0),
-- 8月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 4375.00, 1, 2026, 8, 280000.00, 42000.00, 32000.00, 26900.00, 206000.00, 29750.00, 0),
-- 9月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 4375.00, 1, 2026, 9, 315000.00, 47250.00, 36000.00, 31275.00, 231750.00, 29750.00, 0),
-- 10月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 4375.00, 1, 2026, 10, 350000.00, 52500.00, 40000.00, 35650.00, 257500.00, 29750.00, 0),
-- 11月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 4375.00, 1, 2026, 11, 385000.00, 57750.00, 44000.00, 40025.00, 283250.00, 29750.00, 0),
-- 12月
(@user_id_chen, 35000.00, 5250.00, 4000.00, 4375.00, 1, 2026, 12, 420000.00, 63000.00, 48000.00, 44400.00, 309000.00, 29750.00, 0);

-- 年度汇算
INSERT INTO tax_record (user_id, income, insurance, deduct, tax_amount, calc_type, tax_year, 
                       cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, 
                       taxable_income, before_tax_income, is_delete)
VALUES
(@user_id_chen, 420000.00, 63000.00, 48000.00, 44400.00, 2, 2026, 420000.00, 63000.00, 48000.00, 44400.00, 309000.00, 357000.00, 0);

-- ================================================
-- 4. 用户2：李浩宇 - 中等收入测试数据
-- 月薪：15000元，五险一金：2250元，专项附加扣除：1500元（住房租金）
-- ================================================

-- 专项附加扣除记录
INSERT INTO tax_special_deduct (user_id, deduct_type, status, is_delete)
VALUES 
(@user_id_li, 5, 1, 0);  -- 住房租金

-- 月度预缴数据（1-12月）
INSERT INTO tax_record (user_id, income, insurance, deduct, tax_amount, calc_type, tax_year, tax_month, 
                       cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, 
                       taxable_income, before_tax_income, is_delete)
VALUES
-- 1月
(@user_id_li, 15000.00, 2250.00, 1500.00, 127.50, 1, 2026, 1, 15000.00, 2250.00, 1500.00, 127.50, 11250.00, 12750.00, 0),
-- 2月
(@user_id_li, 15000.00, 2250.00, 1500.00, 127.50, 1, 2026, 2, 30000.00, 4500.00, 3000.00, 255.00, 22500.00, 12750.00, 0),
-- 3月
(@user_id_li, 15000.00, 2250.00, 1500.00, 127.50, 1, 2026, 3, 45000.00, 6750.00, 4500.00, 382.50, 33750.00, 12750.00, 0),
-- 4月
(@user_id_li, 15000.00, 2250.00, 1500.00, 127.50, 1, 2026, 4, 60000.00, 9000.00, 6000.00, 510.00, 45000.00, 12750.00, 0),
-- 5月
(@user_id_li, 15000.00, 2250.00, 1500.00, 127.50, 1, 2026, 5, 75000.00, 11250.00, 7500.00, 637.50, 56250.00, 12750.00, 0),
-- 6月
(@user_id_li, 15000.00, 2250.00, 1500.00, 127.50, 1, 2026, 6, 90000.00, 13500.00, 9000.00, 765.00, 67500.00, 12750.00, 0),
-- 7月
(@user_id_li, 15000.00, 2250.00, 1500.00, 337.50, 1, 2026, 7, 105000.00, 15750.00, 10500.00, 1102.50, 78750.00, 12750.00, 0),
-- 8月
(@user_id_li, 15000.00, 2250.00, 1500.00, 337.50, 1, 2026, 8, 120000.00, 18000.00, 12000.00, 1440.00, 90000.00, 12750.00, 0),
-- 9月
(@user_id_li, 15000.00, 2250.00, 1500.00, 337.50, 1, 2026, 9, 135000.00, 20250.00, 13500.00, 1777.50, 101250.00, 12750.00, 0),
-- 10月
(@user_id_li, 15000.00, 2250.00, 1500.00, 337.50, 1, 2026, 10, 150000.00, 22500.00, 15000.00, 2115.00, 112500.00, 12750.00, 0),
-- 11月
(@user_id_li, 15000.00, 2250.00, 1500.00, 337.50, 1, 2026, 11, 165000.00, 24750.00, 16500.00, 2452.50, 123750.00, 12750.00, 0),
-- 12月
(@user_id_li, 15000.00, 2250.00, 1500.00, 337.50, 1, 2026, 12, 180000.00, 27000.00, 18000.00, 2790.00, 135000.00, 12750.00, 0);

-- 年度汇算
INSERT INTO tax_record (user_id, income, insurance, deduct, tax_amount, calc_type, tax_year, 
                       cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, 
                       taxable_income, before_tax_income, is_delete)
VALUES
(@user_id_li, 180000.00, 27000.00, 18000.00, 2790.00, 2, 2026, 180000.00, 27000.00, 18000.00, 2790.00, 135000.00, 153000.00, 0);

-- ================================================
-- 5. 用户3：丁真 - 低收入测试数据
-- 月薪：6000元，五险一金：900元，无专项附加扣除
-- ================================================

-- 月度预缴数据（1-12月）- 基本不交税
INSERT INTO tax_record (user_id, income, insurance, deduct, tax_amount, calc_type, tax_year, tax_month, 
                       cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, 
                       taxable_income, before_tax_income, is_delete)
VALUES
-- 1月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 1, 6000.00, 900.00, 0.00, 0.00, 5100.00, 5100.00, 0),
-- 2月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 2, 12000.00, 1800.00, 0.00, 0.00, 10200.00, 5100.00, 0),
-- 3月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 3, 18000.00, 2700.00, 0.00, 0.00, 15300.00, 5100.00, 0),
-- 4月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 4, 24000.00, 3600.00, 0.00, 0.00, 20400.00, 5100.00, 0),
-- 5月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 5, 30000.00, 4500.00, 0.00, 0.00, 25500.00, 5100.00, 0),
-- 6月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 6, 36000.00, 5400.00, 0.00, 0.00, 30600.00, 5100.00, 0),
-- 7月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 7, 42000.00, 6300.00, 0.00, 0.00, 35700.00, 5100.00, 0),
-- 8月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 8, 48000.00, 7200.00, 0.00, 0.00, 40800.00, 5100.00, 0),
-- 9月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 9, 54000.00, 8100.00, 0.00, 0.00, 45900.00, 5100.00, 0),
-- 10月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 10, 60000.00, 9000.00, 0.00, 0.00, 51000.00, 5100.00, 0),
-- 11月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 11, 66000.00, 9900.00, 0.00, 0.00, 56100.00, 5100.00, 0),
-- 12月
(@user_id_ding, 6000.00, 900.00, 0.00, 0.00, 1, 2026, 12, 72000.00, 10800.00, 0.00, 0.00, 61200.00, 5100.00, 0);

-- 年度汇算
INSERT INTO tax_record (user_id, income, insurance, deduct, tax_amount, calc_type, tax_year, 
                       cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, 
                       taxable_income, before_tax_income, is_delete)
VALUES
(@user_id_ding, 72000.00, 10800.00, 0.00, 0.00, 2, 2026, 72000.00, 10800.00, 0.00, 0.00, 61200.00, 61200.00, 0);

-- ================================================
-- 6. 查询结果验证
-- ================================================
SELECT '虚伪陈' as user_name, COUNT(*) as record_count, 
       SUM(income) as total_income, SUM(tax_amount) as total_tax
FROM tax_record 
WHERE user_id = @user_id_chen AND tax_year = 2026 AND calc_type = 1;

SELECT '李浩宇' as user_name, COUNT(*) as record_count, 
       SUM(income) as total_income, SUM(tax_amount) as total_tax
FROM tax_record 
WHERE user_id = @user_id_li AND tax_year = 2026 AND calc_type = 1;

SELECT '丁真' as user_name, COUNT(*) as record_count, 
       SUM(income) as total_income, SUM(tax_amount) as total_tax
FROM tax_record 
WHERE user_id = @user_id_ding AND tax_year = 2026 AND calc_type = 1;

SELECT '测试数据插入完成！' as result;
