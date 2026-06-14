package com.yat_sen.ai.domain.agent.service;

import com.yat_sen.ai.domain.agent.model.valobj.AiAgentConfigTableVO;

import java.util.List;

/**
 * 智能体装配服务接口（装配引擎门面）。
 * <p>
 * 领域层对外暴露的"把配置装配成可运行智能体"的入口。预期由
 * {@code AiAgentAutoConfig} 在应用启动后调用，驱动 armory 责任链树完成装配与注册。
 */
public interface IArmoryService {

    /**
     * 接收一批智能体配置表并逐个装配。
     *
     * @param tables 智能体配置表列表，每个元素描述一份完整的智能体应用配置
     * @throws Exception 任一配置装配失败时抛出，由调用方决定是否中断
     */
    void acceptArmoryAgents(List<AiAgentConfigTableVO> tables) throws Exception;

}
