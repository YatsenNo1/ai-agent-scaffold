package com.yat_sen.ai.domain.agent.service.armory.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.yat_sen.ai.domain.agent.model.entity.ArmoryCommandEntity;
import com.yat_sen.ai.domain.agent.service.armory.AbstractArmorySupport;
import com.yat_sen.ai.domain.agent.service.armory.factory.DefaultArmoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AiApi 装配节点。
 * <p>
 * 负责根据配置中的 {@code module.aiApi}（baseUrl / apiKey / 路径等）构建大模型 API 接入点，
 * 并将构建结果写入 {@code DynamicContext} 供后续节点使用。
 * 当前为骨架：装配逻辑待实现（见 {@code doApply} 内 TODO），暂直接向后路由。
 */
@Slf4j
@Service
public class AiApiNode extends AbstractArmorySupport {

    /**
     * 本节点的处理逻辑：构建 AiApi 接入点，再路由到下一节点。
     * 若本节点已是装配终点，可不路由而直接 {@code return} 装配产物。
     */
    @Override
    protected AiAgentRegisterVO doApply(ArmoryCommandEntity requestParameter, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {
        // TODO 编写 AiApi 构建（依据 requestParameter 的配置创建模型 API 客户端并写入 dynamicContext）

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

