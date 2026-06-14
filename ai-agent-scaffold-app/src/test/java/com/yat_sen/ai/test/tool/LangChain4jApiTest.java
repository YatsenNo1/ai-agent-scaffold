package com.yat_sen.ai.test.tool;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class LangChain4jApiTest {

    interface Assistant {
        String chat(String message);
    }

    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.xiaomimimo.com/v1")
                .apiKey("sk-cabc2asc0elzq4h2seehzn8dmps4b94pbwn21h2f2qfbonck")
                .modelName("mimo-v2.5-pro")
                .build();


        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(sseMcpClient())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String answer = assistant.chat("你哪有哪些工具能力");
        log.info("测试结果:{}", answer);

    }


    /**
     * 百度搜索MCP服务(url)；https://sai.baidu.com/zh/detail/e014c6ffd555697deabf00d058baf388
     * 百度搜索MCP服务(key)；https://console.bce.baidu.com/iam/?_=1753597622044#/iam/apikey/list
     */
    public static McpSyncClient sseMcpClient() {

        // 自己申请 api_key
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://appbuilder.baidu.com/v2/ai_search/mcp/")
                .sseEndpoint("sse?api_key=bce-v3/ALTAK-X6PWu2tkt5BbZU6VSqvBS/6d43a15afc18a84fc48e3603b8402b9c88f10cff")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(360)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);

        return mcpSyncClient;
    }

}
