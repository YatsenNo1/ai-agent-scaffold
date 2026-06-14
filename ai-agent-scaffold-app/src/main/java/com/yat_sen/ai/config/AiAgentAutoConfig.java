package com.yat_sen.ai.config;

import com.alibaba.fastjson.JSON;
import com.yat_sen.ai.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.yat_sen.ai.domain.agent.model.valobj.properties.AiAgentAutoConfigProperties;
import jakarta.annotation.Resource;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(AiAgentAutoConfigProperties.class)
public class AiAgentAutoConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private AiAgentAutoConfigProperties aiAgentAutoConfigProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!aiAgentAutoConfigProperties.isEnabled()) {
            log.info("Ai Agent 智能体装配未启用（ai.agent.config.enabled=false），跳过装配");
            return;
        }

        Map<String, AiAgentConfigTableVO> tables = aiAgentAutoConfigProperties.getTables();
        if (tables == null || tables.isEmpty()) {
            log.warn("Ai Agent 智能体装配已启用，但未配置任何 ai.agent.config.tables，跳过装配");
            return;
        }

        try {
            log.info("Ai Agent 智能体装配 {}", JSON.toJSONString(tables.values()));
            // TODO 遍历 tables，根据 AiAgentConfigTableVO 构建并注册智能体运行时（模型客户端、MCP 工具、子智能体、工作流）
        } catch (Exception e) {
            // 装配失败仅记录错误，不阻断应用启动
            log.error("Ai Agent 智能体装配失败", e);
        }
    }

}

