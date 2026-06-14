package com.yat_sen.ai.domain.agent.service.armory.factory;

import com.yat_sen.ai.domain.agent.model.entity.ArmoryCommandEntity;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentRegisterVO;
import com.yat_sen.ai.domain.agent.service.armory.node.RootNode;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * 装配责任链树工厂。
 * <p>
 * 对外暴露 armory 责任链树的入口（根节点 {@link RootNode}），由
 * {@code ArmoryService} 取用后驱动整棵树。同时在内部定义节点间共享的上下文
 * {@link DynamicContext}。
 */
@Service
public class DefaultArmoryFactory {

    @Resource
    private RootNode rootNode;

    /**
     * 获取装配责任链树的入口处理器。
     *
     * @return 根节点 {@link RootNode}，作为 {@code StrategyHandler} 的起点，
     * 后续由各节点的 {@code get()} 依次路由到下一节点
     */
    public StrategyHandler<ArmoryCommandEntity, DynamicContext, AiAgentRegisterVO> armoryStrategyHandler() {
        return rootNode;
    }

    /**
     * 节点间共享的动态上下文。
     * <p>
     * 责任链各节点串联执行时，用它写入/读取中间产物（如已构建的模型客户端、MCP 工具等），
     * 实现跨节点的数据传递。继承 wrench 框架的
     * {@link cn.bugstack.wrench.design.framework.tree.DynamicContext}，
     * 以满足 {@code StrategyHandler} 对上下文类型 {@code D extends DynamicContext} 的泛型约束。
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext extends cn.bugstack.wrench.design.framework.tree.DynamicContext {

        /** 节点间传递的数据容器，key 为约定的数据标识 */
        private Map<String, Object> dataObjects = new HashMap<>();

        /** 写入一个中间产物 */
        public <T> void setValue(String key, T value) {
            dataObjects.put(key, value);
        }

        /** 按 key 读取中间产物（调用方负责类型正确性） */
        public <T> T getValue(String key) {
            return (T) dataObjects.get(key);
        }

    }

}

