# 数学建模助手

一个基于 AI 的数学建模题目分析工具，支持题目分析、模型建议、可行性分析、软件推荐和数据筛选。

## 功能特性

- **多输入方式**：支持文字输入、图片上传、Word 文档
- **多 AI 提供商**：支持 Minimax、OpenAI、Claude、DeepSeek
- **公式渲染**：支持 LaTeX 数学公式渲染
- **数据持久化**：对话记录保存在本地数据库，关闭后不丢失
- **会话管理**：每个浏览器独立会话，历史记录可追溯

## 支持的 AI 提供商

| 提供商 | 模型 | API 格式 |
|--------|------|----------|
| **Minimax** | MiniMax-Text-01 | OpenAI 兼容 |
| **OpenAI** | GPT-4o | OpenAI 标准 |
| **Claude** | 3.5 Sonnet | Anthropic 格式 |
| **DeepSeek** | deepseek-chat | OpenAI 兼容 |

## 技术栈

- **后端**：Spring Boot 3.2 + JPA + H2 数据库
- **前端**：原生 HTML/CSS/JavaScript
- **AI**：Minimax / OpenAI / Claude / DeepSeek
- **公式**：KaTeX
- **构建**：Maven

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+

### 构建

```bash
mvn clean package -DskipTests
```

### 运行

```bash
java -jar target/assistant-1.0.0.jar
```

或双击 `启动.bat`

然后访问 http://localhost:8080

### 停止服务

双击 `停止.bat` 或手动结束 Java 进程

## 使用方法

1. 打开应用后，点击右上角「设置 API Key」选择 AI 提供商并输入 API Key
2. 选择输入方式：
   - **文字输入**：直接输入题目
   - **图片上传**：拖拽或点击上传题目截图
   - **Word 文档**：上传 .docx 格式文件
3. 点击「开始分析」，右侧将显示 AI 分析结果

## 项目结构

```
src/main/java/com/shumo/assistant/
├── MathModelingAssistantApplication.java   # 启动类
├── config/
│   ├── CorsConfig.java                     # CORS 配置
│   └── WebConfig.java                      # WebClient 配置
├── controller/
│   └── ApiController.java                  # REST API 控制器
├── entity/
│   ├── Conversation.java                   # 对话记录实体
│   └── UserSession.java                    # 用户会话实体
├── repository/
│   ├── ConversationRepository.java
│   └── UserSessionRepository.java
└── service/
    ├── AiService.java                      # AI 服务接口
    ├── AiServiceFactory.java               # AI 服务工厂
    ├── ConversationService.java            # 业务逻辑
    ├── MinimaxService.java                 # Minimax AI
    ├── OpenAiService.java                  # OpenAI AI
    ├── ClaudeService.java                  # Claude AI
    └── DeepseekService.java                # DeepSeek AI
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/session` | POST | 创建/获取会话 |
| `/api/chat` | POST | 发送消息并获取 AI 响应 |
| `/api/history/{sessionId}` | GET | 获取对话历史 |
| `/api/apikey/{sessionId}` | GET | 获取会话的 API Key |
| `/api/history/{sessionId}` | DELETE | 清空对话历史 |

## 许可证

MIT License
