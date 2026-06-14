/**
 * 值对象；
 * 1. 用于描述对象属性的值，如一个库表中有json后者一个字段多个属性信息的枚举对象
 * 2. 对象名称如；XxxVO
 */
package com.yat_sen.ai.domain.agent.model.valobj;


import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Ai Agent 智能体配置表值对象。
 * <p>
 * 承载一份 AI 智能体应用的完整配置（通常以 JSON 形式存于库表某字段，读取后反序列化为本对象）。
 * 它定义了"一个 AI Agent 应用 = 模型接入 + MCP 工具 + 子智能体 + 工作流编排"的领域模型。
 * <p>
 * 整体为一棵多层嵌套的配置树：
 * <pre>
 * AiAgentConfigTableVO
 * ├── appName : 应用名称
 * ├── agent   : 智能体身份元信息（{@link MetaAgent}）
 * └── module  : 智能体能力装配（{@link Module}）
 *      ├── aiApi          : 大模型 API 接入点（{@link Module.AiApi}）
 *      ├── chatModel      : 对话模型 + MCP 工具（{@link Module.ChatModel}）
 *      ├── agents         : 子智能体定义列表（{@link Module.SubAgent}）
 *      └── agentWorkflows : 智能体编排工作流（{@link Module.AgentWorkflow}）
 * </pre>
 * 注意：顶层 {@link MetaAgent}（身份元数据）与 {@link Module.SubAgent}（可执行子智能体）职责不同，
 * 前者描述"是哪个智能体"，后者为工作流可编排的运行单元。
 */
@Data
public class AiAgentConfigTableVO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 智能体身份元信息
     */
    private MetaAgent agent;

    /**
     * 智能体能力装配模块
     */
    private Module module;

    /**
     * 智能体身份（顶层）。
     * 描述"这是哪个智能体"的纯元信息，不含运行能力。
     */
    @Data
    public static class MetaAgent {

        /**
         * 智能体ID
         */
        private String agentId;

        /**
         * 智能体名称
         */
        private String agentName;

        /**
         * 智能体描述
         */
        private String agentDesc;

    }

    /**
     * 智能体的能力装配模块。
     * 聚合运行一个 Agent 所需的全部组件：模型接入、工具、子智能体与工作流编排。
     */
    @Data
    public static class Module {

        /**
         * 大模型 API 接入点
         */
        private AiApi aiApi;

        /**
         * 对话模型及其挂载的 MCP 工具
         */
        private ChatModel chatModel;

        /**
         * 子智能体定义列表，供 {@link AgentWorkflow} 按名引用编排
         */
        private List<SubAgent> agents;

        /**
         * 智能体编排工作流列表
         */
        private List<AgentWorkflow> agentWorkflows;

        /**
         * 大模型服务接入配置。
         * 典型的 OpenAI 兼容 API，默认路径表明对接标准 {@code /v1/chat/completions} 协议，
         * baseUrl 可指向任意兼容服务（如本地部署模型）。
         */
        @Data
        public static class AiApi {
            /** 模型服务根地址 */
            private String baseUrl;
            /** 鉴权密钥 */
            private String apiKey;
            /** 对话补全接口路径，默认 OpenAI 兼容路径 */
            private String completionsPath = "/v1/chat/completions";
            /** 向量嵌入接口路径，默认 OpenAI 兼容路径 */
            private String embeddingsPath = "/v1/embeddings";

        }

        /**
         * 对话模型配置，含模型名与挂载的 MCP 工具集。
         */
        @Data
        public static class ChatModel {

            /** 模型名称 */
            private String model;
            /** 挂载的 MCP 工具服务列表 */
            private List<ToolMcp> toolMcpList;

            /**
             * 单个 MCP（Model Context Protocol）工具服务接入配置。
             * 两种接入方式二选一：{@link #sse}（远程 HTTP+SSE）或 {@link #stdio}（本地子进程）。
             */
            @Data
            public static class ToolMcp {

                /** 基于 HTTP+SSE 的远程工具服务 */
                private SSEServerParameters sse;

                /** 基于标准输入输出的本地进程工具服务 */
                private StdioServerParameters stdio;

                /**
                 * SSE 方式的 MCP 服务参数（远程 HTTP+SSE 工具服务）。
                 */
                @Data
                public static class SSEServerParameters {
                    /** 服务名称 */
                    private String name;
                    /** 服务根地址 */
                    private String baseUri;
                    /** SSE 端点路径 */
                    private String sseEndpoint;
                    /** 请求超时（毫秒），默认 3000 */
                    private Integer requestTimeout = 3000;

                }

                /**
                 * Stdio 方式的 MCP 服务参数（以子进程方式拉起本地 MCP server）。
                 */
                @Data
                public static class StdioServerParameters {
                    /** 服务名称 */
                    private String name;
                    /** 请求超时（毫秒），默认 3000 */
                    private Integer requestTimeout = 3000;
                    /** 子进程启动参数 */
                    private ServerParameters serverParameters;

                    /**
                     * 本地子进程的启动参数。
                     */
                    @Data
                    public static class ServerParameters {
                        /** 启动命令 */
                        private String command;
                        /** 命令行参数 */
                        private List<String> args;
                        /** 进程环境变量 */
                        private Map<String, String> env;

                    }
                }

            }
        }

        /**
         * 可执行的子智能体定义。
         * 区别于顶层 {@link AiAgentConfigTableVO.MetaAgent}（身份元数据），此处为运行单元。
         */
        @Data
        public static class SubAgent {
            /** 子智能体名称，作为 {@link AgentWorkflow#subAgents} 的引用标识 */
            private String name;
            /** 系统提示词/指令 */
            private String instruction;
            /** 描述 */
            private String description;
            /** 输出在上下文中的存储键，供工作流串联时取用 */
            private String outputKey;

        }

        /**
         * 智能体编排工作流。
         * 通过 {@link #subAgents}（按名字符串引用 {@link Module.SubAgent}）将多个子智能体
         * 按顺序/并行/循环三种模式编排。注意 subAgents 为弱引用（仅名称），
         * 需在装配时做"名称是否存在"的运行时校验。
         */
        @Data
        public static class AgentWorkflow {
            /**
             * 编排类型；loop（循环）、parallel（并行）、sequential（顺序）
             */
            private String type;
            /** 工作流名称 */
            private String name;
            /** 引用的子智能体名称列表，对应 {@link Module.SubAgent#name} */
            private List<String> subAgents;
            /** 描述 */
            private String description;
            /** 最大迭代次数，主要服务于 loop 类型，默认 3 */
            private Integer maxIterations = 3;

        }
    }

}
