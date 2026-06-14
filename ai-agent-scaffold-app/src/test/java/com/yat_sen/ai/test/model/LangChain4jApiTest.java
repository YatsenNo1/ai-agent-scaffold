package com.yat_sen.ai.test.model;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LangChain4jApiTest {

    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.xiaomimimo.com/v1")
                .apiKey("sk-cabc2asc0elzq4h2seehzn8dmps4b94pbwn21h2f2qfbonck")
                .modelName("mimo-v2.5-pro")
                .build();

        String chat = model.chat("hi 你好哇!");
        log.info("测试结果:{}", chat);
    }

}
