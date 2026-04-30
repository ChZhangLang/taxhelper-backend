package com.zihao.taxhelperai.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class AiServiceImpl implements AiService {

    @Value("${ai.use-real:true}")
    private boolean useReal;

    private final RealAiClient realAiClient;
    private final FakeAiClient fakeAiClient;

    public AiServiceImpl(RealAiClient realAiClient, FakeAiClient fakeAiClient) {
        this.realAiClient = realAiClient;
        this.fakeAiClient = fakeAiClient;
    }

    @Override
    public String call(String prompt) {
        // 开发/调试可手动关闭真实AI，全程本地模拟
        if (!useReal) {
            return fakeAiClient.call(prompt);
        }
        try {
            // 优先调用低成本真实大模型
            return realAiClient.call(prompt);
        } catch (Exception e) {
            // 任何异常自动降级兜底，保证业务不崩
            return fakeAiClient.call(prompt);
        }
    }
}