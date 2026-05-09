package com.zihao.taxhelperai;

import com.zihao.taxhelperai.model.entity.TaxRecord;
import com.zihao.taxhelperai.model.entity.TaxSpecialDeduct;
import com.zihao.taxhelperai.model.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 测试数据生成器（已禁用）
 * 
 * 使用说明：如需生成测试数据，请运行 Python 脚本：
 * python sql/insert_test_data.py
 * 
 * 用户数据设计：
 * - 虚伪陈：高收入 35000/月，3项专项附加扣除
 * - 李浩宇：中等收入 15000/月，1项专项附加扣除  
 * - 丁真：低收入 6000/月，无扣除
 */
@Component
public class TestDataGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 测试数据生成已禁用
        // 如需生成测试数据，请运行 Python 脚本：python sql/insert_test_data.py
    }
}
