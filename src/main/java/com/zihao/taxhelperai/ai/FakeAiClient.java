package com.zihao.taxhelperai.ai;

import org.springframework.stereotype.Service;

@Service
public class FakeAiClient implements AiService {
    @Override
    public String call(String prompt) {
        return "{\"rules\":[{\"name\":\"子女教育\",\"amount\":1000,\"unit\":\"per_month\"}],\"summary\":\"子女教育专项附加扣除，每人每月可定额扣除1000元，用于抵扣个人所得税。\"}";
    }
}