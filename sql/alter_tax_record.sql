-- 修改计税记录表，增加累计字段
USE tax_assistant;

ALTER TABLE tax_record 
ADD COLUMN taxYear INT NOT NULL DEFAULT 2024 COMMENT '计税年度' AFTER calcType,
ADD COLUMN taxMonth INT NOT NULL DEFAULT 1 COMMENT '计税月份（1-12）' AFTER taxYear,
ADD COLUMN cumulativeIncome DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计收入' AFTER taxMonth,
ADD COLUMN cumulativeInsurance DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计五险一金' AFTER cumulativeIncome,
ADD COLUMN cumulativeDeduct DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计专项附加扣除' AFTER cumulativeInsurance,
ADD COLUMN cumulativeTax DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '当年累计已预扣税额' AFTER cumulativeDeduct,
ADD COLUMN taxableIncome DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '应纳税所得额' AFTER cumulativeTax,
ADD COLUMN beforeTaxIncome DECIMAL(16,2) NOT NULL DEFAULT 0 COMMENT '税前收入（月度收入-五险一金）' AFTER taxableIncome;

-- 新增索引
CREATE INDEX idx_taxYear_taxMonth ON tax_record(taxYear, taxMonth);
CREATE INDEX idx_userId_taxYear ON tax_record(userId, taxYear);

-- 更新现有记录的年份和月份（从calcTime提取）
UPDATE tax_record 
SET taxYear = YEAR(calcTime), 
    taxMonth = MONTH(calcTime);

-- 更新现有记录的税前收入
UPDATE tax_record 
SET beforeTaxIncome = income - insurance;

-- 更新现有记录的累计数据（需要按用户和年份分组更新）
UPDATE tax_record tr
JOIN (
    SELECT 
        userId, 
        taxYear, 
        taxMonth,
        @cum_inc := @cum_inc + income AS cum_income,
        @cum_ins := @cum_ins + insurance AS cum_ins,
        @cum_ded := @cum_ded + deduct AS cum_ded,
        @cum_tax := @cum_tax + taxAmount AS cum_tax
    FROM tax_record,
         (SELECT @cum_inc := 0, @cum_ins := 0, @cum_ded := 0, @cum_tax := 0) AS vars
    ORDER BY userId, taxYear, taxMonth
) AS cum_data
ON tr.userId = cum_data.userId 
   AND tr.taxYear = cum_data.taxYear 
   AND tr.taxMonth = cum_data.taxMonth
SET tr.cumulativeIncome = cum_data.cum_income,
    tr.cumulativeInsurance = cum_data.cum_ins,
    tr.cumulativeDeduct = cum_data.cum_ded,
    tr.cumulativeTax = cum_data.cum_tax;