package com.yat_sen.ai.domain.agent.model.valobj;

import lombok.Builder;
import lombok.Getter;

/**
 * 智能体注册结果值对象（装配引擎的产物）。
 * <p>
 * armory 责任链树（{@code RootNode -> AiApiNode -> ...}）逐节点装配完成后，
 * 由各节点的 {@code doApply} 产出并向上返回的"已注册智能体"句柄，
 * 代表一份 {@code AiAgentConfigTableVO} 被成功构建成可运行的智能体运行时。
 * <p>
 * 作为 wrench 策略树
 * {@code StrategyHandler<ArmoryCommandEntity, DynamicContext, AiAgentRegisterVO>}
 * 的返回类型（泛型 {@code R}），与入参 {@code ArmoryCommandEntity}（泛型 {@code T}）一一对应。
 * <p>
 * TODO 当前为空，待补充注册产物字段（如 agentId、模型客户端引用、已挂载的 MCP 工具、
 * 子智能体与工作流的运行时句柄等）。
 */
@Getter
@Builder
//@AllArgsConstructor
//@NoArgsConstructor
public class AiAgentRegisterVO {


}
