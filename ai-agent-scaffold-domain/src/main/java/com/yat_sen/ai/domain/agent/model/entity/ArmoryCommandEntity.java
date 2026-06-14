package com.yat_sen.ai.domain.agent.model.entity;

import com.yat_sen.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 装配命令实体（armory 责任链树的入参）。
 * <p>
 * 包装一份待装配的 {@link AiAgentConfigTableVO} 配置，作为 wrench 策略树
 * {@code StrategyHandler<ArmoryCommandEntity, DynamicContext, AiAgentRegisterVO>}
 * 的请求参数（泛型 {@code T}）在各装配节点间流转；与输出 {@code AiAgentRegisterVO} 对应。
 * <p>
 * 由 {@code ArmoryService.acceptArmoryAgents} 对每个配置表 {@code build} 出一个实例，
 * 各节点（{@code RootNode}/{@code AiApiNode}/...）从中读取配置完成对应组件的构建。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArmoryCommandEntity {

    /** 待装配的智能体配置表 */
    private AiAgentConfigTableVO aiAgentConfigTableVO;

}

