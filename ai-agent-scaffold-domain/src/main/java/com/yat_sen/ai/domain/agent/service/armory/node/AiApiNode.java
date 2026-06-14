package com.yat_sen.ai.domain.agent.service.armory.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.yat_sen.ai.domain.agent.model.entity.ArmoryCommandEntity;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.yat_sen.ai.domain.agent.service.armory.AbstractArmorySupport;
import com.yat_sen.ai.domain.agent.service.armory.factory.DefaultArmoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

/**
 * AiApi 装配节点（默认实现：Spring AI 框架）。
 * <p>
 * 责任链中的大模型 API 接入节点：根据配置中的 {@code module.aiApi}
 * （baseUrl / apiKey / completionsPath / embeddingsPath）与 {@code module.chatModel.model}，
 * 用 <b>Spring AI</b> 构建 {@link OpenAiApi} 并组装出 {@link org.springframework.ai.chat.model.ChatModel}，
 * 写入 {@code DynamicContext}（key 为 {@link #DATA_NAME_AI_CHAT_MODEL}）供后续节点 / 对话服务取用，
 * 随后路由到下一节点。
 * <p>
 * Spring AI 的 {@link OpenAiApi} 采用「baseUrl 根地址 + completionsPath/embeddingsPath 显式路径」的
 * 接入方式，会真正用到配置里的 {@code completionsPath}/{@code embeddingsPath}。
 * <p>
 * LangChain4j 作为备选实现见 {@link LangChain4jApiNode}；如需切换，将 {@code RootNode.get()}
 * 改为返回该备选节点即可。
 */
@Slf4j
@Service
public class AiApiNode extends AbstractArmorySupport {

    /**
     * 构建出的 Spring AI {@link org.springframework.ai.chat.model.ChatModel} 在
     * {@code DynamicContext} 中的存储 key。后续节点（如挂载 MCP 工具的 ChatModel 节点）
     * 或对话服务据此取出复用。
     */
    public static final String DATA_NAME_AI_CHAT_MODEL = "aiChatModel";

    /**
     * 本节点的处理逻辑：依据配置构建 Spring AI {@link ChatModel}，写入上下文后路由到下一节点。
     * 若本节点已是装配终点，可不路由而直接 {@code return} 装配产物。
     */
    @Override
    protected AiAgentRegisterVO doApply(ArmoryCommandEntity requestParameter, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {
        AiAgentConfigTableVO config = requestParameter.getAiAgentConfigTableVO();
        AiAgentConfigTableVO.Module module = config.getModule();

        AiAgentConfigTableVO.Module.AiApi aiApi = module != null ? module.getAiApi() : null;
        if (aiApi == null || aiApi.getBaseUrl() == null || aiApi.getApiKey() == null) {
            throw new IllegalStateException("装配-AiApi 失败：appName=" + config.getAppName() + " 缺少 module.aiApi.baseUrl/apiKey 配置");
        }
        AiAgentConfigTableVO.Module.ChatModel chatModel = module.getChatModel();
        String modelName = chatModel != null ? chatModel.getModel() : null;

        log.info("装配-AiApi 开始(Spring AI), appName:{} baseUrl:{} completionsPath:{} model:{}",
                config.getAppName(), aiApi.getBaseUrl(), aiApi.getCompletionsPath(), modelName);

        // 1) 构建底层 OpenAI 兼容 API 接入点：baseUrl 为根地址，路径单独传入
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(aiApi.getBaseUrl())
                .apiKey(aiApi.getApiKey())
                .completionsPath(aiApi.getCompletionsPath())
                .embeddingsPath(aiApi.getEmbeddingsPath())
                .build();

        // 2) 组装 Spring AI 的 ChatModel，默认选项里指定模型名
        ChatModel springAiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(modelName)
                        .build())
                .build();

        // 3) 写入上下文，供后续节点（ChatModel/McpTool 等）与对话服务取用
        dynamicContext.setValue(DATA_NAME_AI_CHAT_MODEL, springAiChatModel);
        log.info("装配-AiApi 完成(Spring AI), 已写入上下文 key:{}", DATA_NAME_AI_CHAT_MODEL);

        // 路由到下一个节点，如果不需要路由了，可以 return 返回结果
        return router(requestParameter, dynamicContext);
    }

    /**
     * 返回默认处理器作为链路终点（暂无后续节点）。
     * 后续接入新的装配节点（如 ChatModel/McpTool 节点）时，改为返回对应节点即可延长链路。
     */
    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryFactory.DynamicContext, AiAgentRegisterVO> get(ArmoryCommandEntity requestParameter, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {
        // 如果不需要下一个节点了，可以配置 defaultStrategyHandler
        return defaultStrategyHandler;
    }

}
