package com.zihao.taxhelperai.ai;

public interface AiService {
    /**
     * 调用AI服务
     * @param prompt 提示词
     * @return AI返回结果
     */
    String call(String prompt);
}