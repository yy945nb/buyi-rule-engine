# buyi-rule-engine

规则引擎 + AI 网关一体化平台。统一 JDK 21 + Spring Boot 3.5.5，整合规则引擎与 AI Gateway，提供 OpenAI 兼容的模型代理、MCP 协议网关、可视化工作流编排等能力。

## 技术栈

| 层级 | 技术 |
|------|------|
| JDK | 21 |
| 后端框架 | Spring Boot 3.5.5 / Spring Cloud 2024.0.3 / Spring Cloud Alibaba 2023.0.3.2 |
| 数据库 | MySQL (MyBatis-Plus / Druid) + Flyway |
| 缓存/消息 | Redis (Redisson) + RabbitMQ |
| 规则引擎 | JEXL3 / GraalVM JS / Groovy / Aviator |
| 前端 | Vue 3.5 + TypeScript + Vite 8 + Pinia 3 + Element Plus |
| 微前端 | wujie (嵌入 React 工作流编辑器) |
| 注册/配置中心 | Nacos |

## 项目结构

```
buyi-rule-engine/
├── pom.xml                          # Parent POM (统一版本管理)
│
├── buyi-gateway-sdk/                # AI Gateway SDK (纯库，无依赖)
├── buyi-gateway-mcp/                # MCP 协议接口与模型定义
├── buyi-gateway-app/                # AI Gateway 可启动应用 (WebFlux)
│
├── buyi-rule-engine-core/           # 规则引擎核心 (表达式引擎、领域模型)
├── buyi-rule-engine-compute/        # 规则计算执行器
├── buyi-rule-engine-web/            # 规则引擎 Web 应用 (MVC, 可启动)
│
└── frontend/                        # Vue 3 管理后台
    └── workflow-editor/             # React 工作流编辑器 (独立构建)
```

### 模块依赖关系

```
buyi-gateway-sdk  ←──┐
buyi-gateway-mcp  ←──┼── buyi-gateway-app      (可启动，端口 8080)
                     │
buyi-rule-engine-core ←── buyi-rule-engine-compute ←── buyi-rule-engine-web (可启动，端口 8081)
  └── buyi-gateway-sdk
```

两组应用完全独立，各自打包为 Spring Boot fat JAR。

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- Node.js 20+ / npm 10+
- MySQL 8.0+
- Redis 7+

### 后端构建

```bash
# 编译全部模块
mvn clean package -DskipTests

# 仅编译规则引擎
mvn clean package -DskipTests -pl buyi-rule-engine-core,buyi-rule-engine-compute,buyi-rule-engine-web

# 仅编译 AI Gateway
mvn clean package -DskipTests -pl buyi-gateway-sdk,buyi-gateway-mcp,buyi-gateway-app
```

### 前端开发

```bash
cd frontend
npm install
npm run dev          # 开发服务器 → http://localhost:5173
npm run build        # 生产构建
npm run type-check   # TypeScript 类型检查
```

### 启动应用

```bash
# 规则引擎 Web
java -jar buyi-rule-engine-web/target/buyi-rule-engine-web-1.0-SNAPSHOT.jar

# AI Gateway
java -jar buyi-gateway-app/target/buyi-gateway-app-1.0-SNAPSHOT.jar
```

## 配置说明

### 环境变量 (前端)

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `VITE_CHILD_APP_URL` | 工作流编辑器子应用地址 | `http://localhost:3000` |

复制 `frontend/.env.example` 为 `frontend/.env` 进行配置。

### 后端配置

- **规则引擎**: `buyi-rule-engine-web/src/main/resources/application.yml`
- **AI Gateway**: `buyi-gateway-app/src/main/resources/application.yml`

两者使用独立数据库，零表重叠。

## 管理后台功能

| 分区 | 页面 | 说明 |
|------|------|------|
| 概览 | 仪表盘 | 请求量、错误率、延迟等核心指标 |
| AI 网关 | 提供商管理 | 接入通道与模型路由配置 |
| | 模型配置 | auto 智能路由与 /v1/models 返回列表 |
| | API Key 配置 | 密钥创建、状态、限额、过期管理 |
| 工作流 | 工作流管理 | CRUD 工作流，查看执行日志 |
| | 模板管理 | 工作流模板的增删改查 |
| | 工作流编辑器 | 可视化拖拽编排 (wujie 嵌入 React 子应用) |
| MCP 网关 | 服务管理 | MCP 服务注册与健康检查 |
| | 工具管理 | MCP 工具定义与测试 |
| | 路由规则 | 基于工具名/关键词的路由匹配 |
| | 能力注册 | 服务能力标签与权重管理 |
| | 统计看板 | 实时/历史调用统计 |
| 监控 | 请求日志 | API 请求历史与链路追踪 |
| | 系统监控 | CPU/内存/JVM/线程池运行时指标 |

## API 兼容性

### OpenAI 兼容端点 (AI Gateway)

```
POST /v1/chat/completions      # Chat Completion
POST /v1/completions           # Text Completion
POST /v1/embeddings            # Embeddings
GET  /v1/models                # 模型列表
```

### MCP 协议端点 (AI Gateway)

```
POST /mcp/tools/call           # 工具调用
GET  /mcp/tools/list           # 工具列表
POST /mcp/sse                  # SSE 流式通信
```

## 开发计划

### Phase 1: 基础设施完善

- [ ] 统一日志框架 (SLF4J + Logback 统一配置)
- [ ] 统一异常处理与错误码规范
- [ ] 接口幂等性与限流 (Redis + 注解)
- [ ] 数据库 Flyway 迁移脚本规范化
- [ ] 单元测试覆盖率提升 (>60%)

### Phase 2: 规则引擎增强

- [ ] 规则版本灰度发布 (A/B 测试)
- [ ] 规则执行链路追踪与性能分析
- [ ] 规则热加载 (无需重启)
- [ ] SQL 规则执行器优化 (连接池、批量执行)
- [ ] 规则模板市场 (预置常用规则模板)

### Phase 3: AI Gateway 能力扩展

- [ ] 多模型 Fallback 策略 (优先级 + 权重 + 健康检查)
- [ ] Token 用量统计与配额管理
- [ ] 请求/响应缓存 (语义去重)
- [ ] 流式输出 SSE 断线重连
- [ ] Provider API Key 自动轮换
- [ ] 支持更多模型协议 (Anthropic / Gemini 原生协议)

### Phase 4: MCP 网关完善

- [ ] MCP 工具动态注册与热更新
- [ ] MCP 协议 v2 支持 (Resources / Prompts)
- [ ] 工具调用链路追踪
- [ ] 工具执行沙箱隔离
- [ ] MCP 服务自动发现 (Nacos 集成)

### Phase 5: 工作流引擎

- [ ] 工作流定时触发 (Cron 表达式)
- [ ] 工作流并行分支执行
- [ ] 工作流变量传递与条件表达式
- [ ] 工作流执行历史回放
- [ ] 工作流导入/导出 (JSON / YAML)

### Phase 6: 前端优化

- [ ] 国际化 (i18n) 支持
- [ ] 暗色主题
- [ ] 移动端适配
- [ ] 前端 E2E 测试 (Playwright)
- [ ] 工作流编辑器组件库扩展

### Phase 7: 部署与运维

- [ ] Docker Compose 一键部署
- [ ] Kubernetes Helm Chart
- [ ] CI/CD 流水线 (GitHub Actions)
- [ ] 监控告警集成 (Prometheus + Grafana)
- [ ] 日志聚合 (ELK / Loki)

## License

Private
