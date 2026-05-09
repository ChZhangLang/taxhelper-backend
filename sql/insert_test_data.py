import pymysql
import time

config = {
    'host': 'localhost',
    'user': 'root',
    'password': '1234',
    'database': 'tax_assistant',
    'charset': 'utf8mb4'
}

conn = pymysql.connect(**config)
cursor = conn.cursor()

def get_next_id():
    return int(time.time() * 1000000) + hash(time.time()) % 1000000

current_time = time.strftime('%Y-%m-%d %H:%M:%S')

try:
    user1_id = 2052996603137323009  # 虚伪陈
    user2_id = 2052996603191848962  # 李浩宇
    user3_id = 2052996603254763521  # 丁真

    print("User IDs loaded successfully")

    # 用户1：高收入 35000/月 - 3项扣除
    cursor.execute("DELETE FROM tax_record WHERE userId = %s AND taxYear = 2026", (user1_id,))
    cursor.execute("DELETE FROM tax_special_deduct WHERE user_id = %s", (user1_id,))
    
    cursor.execute("INSERT INTO tax_special_deduct (id, user_id, deduct_type, start_date, end_date, status, create_time, update_time, is_delete) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", (get_next_id(), user1_id, 1, '2026-01-01', '2026-12-31', 1, current_time, current_time, 0))
    cursor.execute("INSERT INTO tax_special_deduct (id, user_id, deduct_type, start_date, end_date, status, create_time, update_time, is_delete) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", (get_next_id(), user1_id, 4, '2026-01-01', '2026-12-31', 1, current_time, current_time, 0))
    cursor.execute("INSERT INTO tax_special_deduct (id, user_id, deduct_type, start_date, end_date, status, create_time, update_time, is_delete) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", (get_next_id(), user1_id, 6, '2026-01-01', '2026-12-31', 1, current_time, current_time, 0))
    
    cumulative_income = 0
    cumulative_insurance = 0
    cumulative_deduct = 0
    cumulative_tax = 0
    
    for month in range(1, 13):
        cumulative_income += 35000
        cumulative_insurance += 5250
        cumulative_deduct += 4000
        
        taxable_income = cumulative_income - cumulative_insurance - cumulative_deduct - 5000 * month
        temp_tax = 0
        
        if taxable_income > 0:
            if taxable_income <= 36000:
                temp_tax = taxable_income * 0.03
            elif taxable_income <= 144000:
                temp_tax = taxable_income * 0.10 - 2520
            elif taxable_income <= 300000:
                temp_tax = taxable_income * 0.20 - 16920
            else:
                temp_tax = taxable_income * 0.25 - 31920
        
        month_tax = max(0, temp_tax - cumulative_tax)
        cumulative_tax += month_tax
        
        cursor.execute("""
            INSERT INTO tax_record (userId, income, insurance, deduct, taxAmount, calcType, taxYear, taxMonth, 
            cumulativeIncome, cumulativeInsurance, cumulativeDeduct, cumulativeTax, taxableIncome, beforeTaxIncome, isDelete)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (user1_id, 35000, 5250, 4000, month_tax, 1, 2026, month, cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, taxable_income, 35000-5250, 0))
    
    cursor.execute("""
        INSERT INTO tax_record (userId, income, insurance, deduct, taxAmount, calcType, taxYear, 
        cumulativeIncome, cumulativeInsurance, cumulativeDeduct, cumulativeTax, taxableIncome, beforeTaxIncome, isDelete)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (user1_id, 420000, 63000, 48000, cumulative_tax, 2, 2026, 420000, 63000, 48000, cumulative_tax, 420000-63000-48000-60000, 420000-63000, 0))
    
    conn.commit()
    print(f"User 1 (虚伪陈) - High income: tax records created, total tax: {cumulative_tax}")

    # 用户2：中等收入 15000/月 - 1项扣除
    cursor.execute("DELETE FROM tax_record WHERE userId = %s AND taxYear = 2026", (user2_id,))
    cursor.execute("DELETE FROM tax_special_deduct WHERE user_id = %s", (user2_id,))
    
    cursor.execute("INSERT INTO tax_special_deduct (id, user_id, deduct_type, start_date, end_date, status, create_time, update_time, is_delete) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", (get_next_id(), user2_id, 5, '2026-01-01', '2026-12-31', 1, current_time, current_time, 0))
    
    cumulative_income = 0
    cumulative_insurance = 0
    cumulative_deduct = 0
    cumulative_tax = 0
    
    for month in range(1, 13):
        cumulative_income += 15000
        cumulative_insurance += 2250
        cumulative_deduct += 1500
        
        taxable_income = cumulative_income - cumulative_insurance - cumulative_deduct - 5000 * month
        temp_tax = 0
        
        if taxable_income > 0:
            if taxable_income <= 36000:
                temp_tax = taxable_income * 0.03
            elif taxable_income <= 144000:
                temp_tax = taxable_income * 0.10 - 2520
            else:
                temp_tax = taxable_income * 0.20 - 16920
        
        month_tax = max(0, temp_tax - cumulative_tax)
        cumulative_tax += month_tax
        
        cursor.execute("""
            INSERT INTO tax_record (userId, income, insurance, deduct, taxAmount, calcType, taxYear, taxMonth, 
            cumulativeIncome, cumulativeInsurance, cumulativeDeduct, cumulativeTax, taxableIncome, beforeTaxIncome, isDelete)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (user2_id, 15000, 2250, 1500, month_tax, 1, 2026, month, cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, taxable_income, 15000-2250, 0))
    
    cursor.execute("""
        INSERT INTO tax_record (userId, income, insurance, deduct, taxAmount, calcType, taxYear, 
        cumulativeIncome, cumulativeInsurance, cumulativeDeduct, cumulativeTax, taxableIncome, beforeTaxIncome, isDelete)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (user2_id, 180000, 27000, 18000, cumulative_tax, 2, 2026, 180000, 27000, 18000, cumulative_tax, 180000-27000-18000-60000, 180000-27000, 0))
    
    conn.commit()
    print(f"User 2 (李浩宇) - Middle income: tax records created, total tax: {cumulative_tax}")

    # 用户3：低收入 6000/月 - 无扣除
    cursor.execute("DELETE FROM tax_record WHERE userId = %s AND taxYear = 2026", (user3_id,))
    cursor.execute("DELETE FROM tax_special_deduct WHERE user_id = %s", (user3_id,))
    
    cumulative_income = 0
    cumulative_insurance = 0
    cumulative_deduct = 0
    cumulative_tax = 0
    
    for month in range(1, 13):
        cumulative_income += 6000
        cumulative_insurance += 900
        cumulative_deduct += 0
        
        taxable_income = cumulative_income - cumulative_insurance - cumulative_deduct - 5000 * month
        month_tax = 0
        
        cursor.execute("""
            INSERT INTO tax_record (userId, income, insurance, deduct, taxAmount, calcType, taxYear, taxMonth, 
            cumulativeIncome, cumulativeInsurance, cumulativeDeduct, cumulativeTax, taxableIncome, beforeTaxIncome, isDelete)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (user3_id, 6000, 900, 0, month_tax, 1, 2026, month, cumulative_income, cumulative_insurance, cumulative_deduct, cumulative_tax, taxable_income, 6000-900, 0))
    
    cursor.execute("""
        INSERT INTO tax_record (userId, income, insurance, deduct, taxAmount, calcType, taxYear, 
        cumulativeIncome, cumulativeInsurance, cumulativeDeduct, cumulativeTax, taxableIncome, beforeTaxIncome, isDelete)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (user3_id, 72000, 10800, 0, 0, 2, 2026, 72000, 10800, 0, 0, 72000-10800-60000, 72000-10800, 0))
    
    conn.commit()
    print(f"User 3 (丁真) - Low income: tax records created, total tax: {cumulative_tax}")
    
    print("="*60)
    print("All test data inserted successfully!")
    
    cursor.execute("""
        SELECT u.realName, COUNT(*) as records, 
               SUM(tr.income) as total_income, SUM(tr.taxAmount) as total_tax
        FROM tax_record tr 
        JOIN user u ON tr.userId = u.id 
        WHERE tr.taxYear = 2026 AND tr.calcType = 1
        GROUP BY u.realName
    """)
    results = cursor.fetchall()
    print("\nData verification (Monthly records):")
    for row in results:
        print(f"  {row[0]}: {row[1]} records, Total Income: {row[2]}, Total Tax: {row[3]}")

except Exception as e:
    print(f"Error: {e}")
    conn.rollback()
finally:
    cursor.close()
    conn.close()
