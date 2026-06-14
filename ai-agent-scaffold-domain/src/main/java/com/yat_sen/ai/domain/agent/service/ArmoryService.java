package com.yat_sen.ai.domain.agent.service;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.yat_sen.ai.domain.agent.model.entity.ArmoryCommandEntity;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.yat_sen.ai.domain.agent.service.armory.factory.DefaultArmoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 智能体装配服务实现（装配引擎门面实现）。
 * <p>
 * 遍历每一份配置表，从 {@link DefaultArmoryFactory} 取得 armory 责任链树的根节点处理器，
 * 以 {@link ArmoryCommandEntity} 为入参、{@code DynamicContext} 为节点间上下文驱动整棵树，
 * 逐节点完成模型接入、MCP 工具、子智能体、工作流等运行时组件的构建与注册。
 */
@Slf4j
@Service
public class ArmoryService implements IArmoryService {

    @Resource
    private DefaultArmoryFactory defaultArmoryFactory;

    @Override
    public void acceptArmoryAgents(List<AiAgentConfigTableVO> tables) throws Exception {
        // 每份配置表独立装配一次
        for (AiAgentConfigTableVO table : tables) {
            // 取得责任链树的入口处理器（根节点 RootNode）
            StrategyHandler<ArmoryCommandEntity, DefaultArmoryFactory.DynamicContext, AiAgentRegisterVO> handler = defaultArmoryFactory.armoryStrategyHandler();
            // 将配置包装为命令实体，连同一个全新的上下文交给责任链树执行装配
            handler.apply(
                    ArmoryCommandEntity.builder()
                            .aiAgentConfigTableVO(table)
                            .build(),
                    new DefaultArmoryFactory.DynamicContext());
        }
    }

}

