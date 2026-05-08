package com.zihao.taxhelperai.config;

import com.zihao.taxhelperai.service.rule.DeductRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 专项附加扣除规则引擎配置
 */
@Configuration
public class DeductRuleConfig {

    /**
     * 构建扣除类型到规则的映射
     * 使用 getType() 方法的返回值作为 key
     */
    @Bean
    public Map<Integer, DeductRule> deductRuleMap(List<DeductRule> rules) {
        return rules.stream()
                .collect(Collectors.toMap(
                        DeductRule::getType,
                        rule -> rule,
                        (existing, replacement) -> existing
                ));
    }
}
