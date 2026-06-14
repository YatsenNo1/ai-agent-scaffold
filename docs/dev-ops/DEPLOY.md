# 部署说明

本文档说明 `ai-agent-scaffold` 在不同环境下的启动方式，重点是 **AI Agent 智能体配置** 所需的环境变量。

## 1. 配置 Profile 与 Agent 文件的对应关系

| Profile | 主配置 | 导入的 Agent 配置 | 密钥来源 |
| --- | --- | --- | --- |
| `dev` | `application-dev.yml` | `agent/test-agent.yml` | 明文（本地调试） |
| `test` | `application-test.yml` | `agent/test-agent.yml` | 明文（共用测试配置） |
| `prod` | `application-prod.yml` | `agent/prod-agent.yml` | **环境变量占位 `${VAR}`** |

`enabled: true` 时才会触发 `AiAgentAutoConfig` 的装配逻辑；`prod-agent.yml` 已默认开启。

## 2. prod 环境所需的环境变量

`prod-agent.yml` 把敏感值都写成了 `${VAR}` 占位，启动前必须注入。

### 必填（缺失则启动期 fail-fast）

| 变量 | 说明 |
| --- | --- |
| `AI_API_KEY` | 大模型 API 鉴权密钥 |
| `BAIDU_SEARCH_API_KEY` | 百度 AI 搜索 MCP 的 api_key |
| `BAIDU_MAP_AK` | 百度地图 MCP 的 ak |

> 这些占位无默认值，未注入时 Spring 会抛 `Could not resolve placeholder`，应用启动失败。
> 这是有意为之，避免 prod 拿空密钥静默运行。

### 选填（不设置则用 `prod-agent.yml` 中的默认值）

| 变量 | 默认值 |
| --- | --- |
| `AI_API_BASE_URL` | `https://apis.itedus.cn` |
| `AI_API_COMPLETIONS_PATH` | `v1/chat/completions` |
| `AI_API_EMBEDDINGS_PATH` | `v1/embeddings` |
| `AI_CHAT_MODEL` | `gpt-4.1` |

## 3. Docker Compose 部署

1. 复制环境变量样例并填入真实值：

   ```bash
   cd docs/dev-ops
   cp .env.example .env
   vim .env   # 填入 AI_API_KEY 等真实密钥
   ```

2. 启动（`docker-compose` 会自动读取同目录下的 `.env`）：

   ```bash
   docker-compose -f docker-compose-app.yml up -d
   ```

   `docker-compose-app.yml` 已通过 `SPRING_PROFILES_ACTIVE=prod` 激活 prod 配置，
   并把上述环境变量透传给容器。

3. 查看日志确认装配：

   ```bash
   docker logs -f ai-agent-scaffold
   ```

   - 成功：日志打印 `Ai Agent 智能体装配 [...]`
   - 未启用：`Ai Agent 智能体装配未启用（ai.agent.config.enabled=false），跳过装配`

## 4. 直接用 jar 启动（非容器）

```bash
export AI_API_KEY=sk-xxxx
export BAIDU_SEARCH_API_KEY=bce-v3/xxxx
export BAIDU_MAP_AK=xxxx
java -jar ai-agent-scaffold-app.jar --spring.profiles.active=prod
```

## 5. 安全注意事项

- **不要**把真实密钥提交到 git。`.env` 已被 `.gitignore` 忽略，仓库只保留 `.env.example`。
- prod 默认沿用了测试智能体的 `app-name=testAgent`、`agent-id=100001`，正式上线请改为正式标识。
- 密钥轮换后只需更新 `.env` 并 `docker-compose up -d` 重建容器，无需改动代码或镜像。