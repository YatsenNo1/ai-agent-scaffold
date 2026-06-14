package com.yat_sen.ai.domain.agent.service.armory.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.yat_sen.ai.domain.agent.model.entity.ArmoryCommandEntity;
import com.yat_sen.ai.domain.agent.service.armory.AbstractArmorySupport;
import com.yat_sen.ai.domain.agent.service.armory.factory.DefaultArmoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 装配责任链树的起点节点。
 * <p>
 * 作为整棵 armory 树的入口（由 {@code DefaultArmoryFactory.armoryStrategyHandler()} 返回）。
 * 本身不做具体装配，仅承担"开链"职责：直接路由到下一节点 {@link AiApiNode}。
 * 后续可在此扩展全局校验、上下文初始化等前置逻辑。
 */
@Slf4j
@Service
public class RootNode extends AbstractArmorySupport {

    @Resource
    private AiApiNode aiApiNode;

    /**
     * 本节点的处理逻辑：不构建任何组件，直接路由到下一节点。
     * {@code router(...)} 为父类提供的路由方法，会调用 {@link #get} 取下一节点并执行。
     */
    @Override
    protected AiAgentRegisterVO doApply(ArmoryCommandEntity requestParameter, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {

        // 路由到下一个节点
        return router(requestParameter, dynamicContext);
    }

    /**
     * 指定下一节点为 {@link AiApiNode}。
     */
    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryFactory.DynamicContext, AiAgentRegisterVO> get(ArmoryCommandEntity requestParameter, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {
        // 配置了下一个节点
        return aiApiNode;
    }

}

