package com.yat_sen.ai.test.tool;


import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.time.Duration;


/**
 * Spring AI Test
 * 文档：<a href="https://docs.spring.io/spring-ai/reference/1.0/api/advisors.html">spring ai</a>
 */
@Slf4j
public class SpringAiApiTest {

    public static void main(String[] args) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://api.xiaomimimo.com")
                .apiKey("sk-cabc2asc0elzq4h2seehzn8dmps4b94pbwn21h2f2qfbonck")
                .completionsPath("/v1/chat/completions")
                .embeddingsPath("v1/embeddings")
                .build();

        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("mimo-v2.5-pro")
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient())
                                .getToolCallbacks())
                        .build())

                .build();

        String call = chatModel.call("你有哪些工具能力");

        log.info("测试结果:{}", call);
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
