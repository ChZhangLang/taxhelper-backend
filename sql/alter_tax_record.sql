-- 修改计税记录表，增加累计字段
USE tax_assistant;

ALTER TABLE t_tax_record 
ADD COLUMN tax_year INT NOT NULL DEFAULT 2024 COMMENT '计税年度' AFTER calc_type,
ADD COLUMN tax_month INT NOT NULL DEFAULT 1 COMMENT '计税月份（1-12）' AFTER tax_year,
ADD COLUMN cumulative_income DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计收入' AFTER tax_month,
ADD COLUMN cumulative_insurance DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计五险一金' AFTER cumulative_income,
ADD COLUMN cumulative_deduct DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计专项附加扣除' AFTER cumulative_insurance,
ADD COLUMN cumulative_tax DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计已预扣税额' AFTER cumulative_deduct,
ADD COLUMN taxable_income DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '应纳税所得额' AFTER cumulative_tax,
ADD COLUMN before_tax_income DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '税前收入（月度收入-五险一金）' AFTER taxable_income;

-- 新增索引
CREATE INDEX idx_tax_year_month ON t_tax_record(tax_year, tax_month);
CREATE INDEX idx_user_year ON t_tax_record(user_id, tax_year);

-- 更新现有记录的年份和月份（从calc_time提取）
UPDATE t_tax_record 
SET tax_year = YEAR(calc_time), 
    tax_month = MONTH(calc_time);

-- 更新现有记录的税前收入
UPDATE t_tax_record 
SET before_tax_income = income - insurance;

-- 更新现有记录的累计数据（需要按用户和年份分组更新）
UPDATE t_tax_record tr
JOIN (
    SELECT 
        user_id, 
        tax_year, 
        tax_month,
        @cum_inc := @cum_inc + income AS cum_income,
        @cum_ins := @cum_ins + insurance AS cum_ins,
        @cum_ded := @cum_ded + deduct AS cum_ded,
        @cum_tax := @cum_tax + tax_amount AS cum_tax
    FROM t_tax_record,
         (SELECT @cum_inc := 0, @cum_ins := 0, @cum_ded := 0, @cum_tax := 0) AS vars
    ORDER BY user_id, tax_year, tax_month
) AS cum_data
ON tr.user_id = cum_data.user_id 
   AND tr.tax_year = cum_data.tax_year 
   AND tr.tax_month = cum_data.tax_month
SET tr.cumulative_income = cum_data.cum_income,
    tr.cumulative_insurance = cum_data.cum_ins,
    tr.cumulative_deduct = cum_data.cum_ded,
    tr.cumulative_tax = cum_data.cum_tax;