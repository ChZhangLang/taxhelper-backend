package com.zihao.taxhelperai.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RealAiClient {

    @Value("${ai.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // 通义千问兼容模式请求地址
    private static final String URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    public RealAiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String call(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        // 强制锁定低价轻量模型，控制毕设成本
        body.put("model", "qwen3.6-flash");
        // 降低随机性，保证JSON输出稳定、格式统一
        body.put("temperature", 0.05);

        // 修复后：JDK 8 完美运行
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("role", "user");
        map.put("content", prompt);
        messages.add(map);
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(URL, request, String.class);

        return parse(response.getBody());
    }

    // 统一解析返回内容，提取纯回答
    private String parse(String body) {
        JSONObject json = JSON.parseObject(body);
        return json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}