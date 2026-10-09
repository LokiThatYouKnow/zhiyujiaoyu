# 智语教育 · 基于大模型的智慧教学实训平台

> **不是聊天框，而是替老师组织知识、出题、排课，替学生出题、批改、抓错题的教学 Agent：课件丢进 RAG 就能生成题库，大模型批改结果直接结构化入库并进数据看板。**
>
> 面向高校"教、学、练、评、管"全流程的一体化平台，Vue3 + Spring Boot + FastAPI 三服务协同，全部可本地部署。

![Vue](https://img.shields.io/badge/Vue-3.5-42b883)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6db33f)
![FastAPI](https://img.shields.io/badge/FastAPI-0.115-009688)
![LLM](https://img.shields.io/badge/LLM-DeepSeek%20V4%20Pro-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 一、项目简介

**智语教育**把"大模型能力"嵌进真实教学流程，而不是做一个聊天框：

- **教师侧**：上传课件/教材 → 自动切分入库（RAG）→ 生成题库、试卷、作业、课程知识点结构 → 排课与时长安排
- **学生侧**：按课程/知识点自主练习、提交作答、大模型自动批改与讲评、错题集中展示
- **管理侧**：课程与班级管理、考勤（签到/统计/导出）、点击与耗时埋点、多维数据看板

系统采用**三服务分层**：Java 后端负责业务与权限，Python 服务负责大模型与向量检索，前端负责交互与可视化。

---

## 二、系统架构

```
┌────────────────────────────┐
│  A7vue3Project (Vue3/Vite) │  :9070
│  Ant Design Vue + Pinia    │
│  ECharts 数据看板 / 文档导出 │
└─────────────┬──────────────┘
              │ HTTP (Axios + JWT)
┌─────────────▼──────────────┐        ┌──────────────────────────┐
│  A7SpringBoot (Spring Boot)│  HTTP  │ langchain_service        │
│  :9080                     │───────▶│ (FastAPI + LangChain)    │
│  Spring Security + JWT     │        │ :9084                    │
│  MyBatis-Plus / JPA        │        │  ├─ Chains: teacher/student
│  Redis 缓存 · MinIO 对象存储│        │  ├─ RAG: 切分 / 向量入库  │
│  POI 文档解析与导出         │        │  └─ LLM: DeepSeek V4 Pro │
└─────────────┬──────────────┘        └───────┬──────────────────┘
              │                               │
   ┌──────────▼──────────┐          ┌─────────▼─────────┐
   │ MySQL (库 A7)       │          │ ChromaDB 向量库    │
   │ Redis (缓存/过期监听)│          │ Ollama Embedding   │
   └─────────────────────┘          └───────────────────┘
```

**技术栈**

| 层 | 技术 |
| --- | --- |
| 前端 | Vue 3.5 + TypeScript + Vite 6 + Pinia + Vue Router + Ant Design Vue 4 + Tailwind CSS 4 + ECharts；marked + DOMPurify 渲染富文本；docx / docxtemplater / jspdf / jszip 做导出 |
| 后端 | Spring Boot 3.5（Java 17）+ Spring Security + JWT + MyBatis-Plus 3.5 + JPA + Redis（含 Key 过期监听）+ MinIO + Hutool + POI / flexmark / jsoup（课件解析）+ OkHttp |
| AI 服务 | FastAPI + LangChain 0.3 + langchain-openai / langchain-ollama / langchain-chroma + ChromaDB + sentence-transformers + pypdf / python-docx |
| 大模型 | DeepSeek V4 Pro（云端 API，可选 deepseek-chat） |
| Embedding | Ollama 本地 `nomic-embed-text`（无需 GPU） |
| 基础设施 | MySQL 8 · Redis · MinIO · ChromaDB |

---

## 三、功能模块

**教师端**（`/api/langchain/teacher/*`）
| 接口 | 功能 |
| --- | --- |
| `knowledgeOrganization` | 课件/教材自动组织成课程知识结构 |
| `addCourseKnowledge` / `deleteCourse` | 课程知识库增删 |
| `practiseDesign` / `designIssue` | 生成练习与题目（按知识点/难度） |
| `getIssueList` / `getIssueAnswerDetail` / `deleteIssue` | 题库管理与答案解析 |
| `searchCourseware` | 课件语义检索（RAG） |
| `arrangeTime` | 教学时长/进度安排 |
| `getResourceList` / `deleteResource` | 资源管理 |

**学生端**（`/api/langchain/student/*`）
| 接口 | 功能 |
| --- | --- |
| `selfPractise` / `selfPractiseCorrect` | 自主练习 + 自动批改 |
| `coursePractise` / `coursePractiseCorrectAnswer` | 课程练习 + 标准答案讲评 |
| `answer` | 单题作答与判分 |

**通用**：`/health`（健康检查）、`/knowledge/collections`（向量库列表）、`/models`（可用模型）、`/chat`（通用对话）

**管理端**（Spring Boot 侧）：登录/注册与鉴权（`AuthController`）、学生/教师/管理员管理、考勤（`AttendanceController`）、文档转换（`ConversionController`）、短信（`SmsController`）、数据埋点与看板。

---

## 四、RAG 实现要点

- **知识源**：`langchain_service/knowledge_base/courses/*.md`（Markdown 课件，可直接放教材/讲义）
- **切分**：`CHUNK_SIZE=1000`、`CHUNK_OVERLAP=200`（`config.py` 可调）
- **向量库**：ChromaDB 持久化在 `langchain_service/chroma_db/`，**按课程名分 collection**，支持多课程隔离
- **检索**：`retrieve_context(course_name, query, k=6)`，把命中的片段拼进 Prompt
- **文档解析**：支持 `.pdf / .docx / .txt / .md`，单文件上限 50 MB
- **Embedding**：走本地 Ollama（`nomic-embed-text`），**不依赖 GPU、不额外花钱**；生成模型走 DeepSeek 云端 API

---

## 五、目录结构

```
zhiyujiaoyu/
├── A7SpringBoot/                 # Java 后端（Spring Boot 3.5）
│   ├── src/main/java/com/mengzhidui/a7springboot/
│   │   ├── controller/           # Auth / Student / Teacher / Admin / Attendance / Conversion / Sms
│   │   ├── service/              # 业务服务，含 LangChainClientService（调用 AI 服务）
│   │   ├── entity/{po,dto,vo}/   # 持久化 / 传输 / 视图对象
│   │   ├── mapper/               # MyBatis-Plus Mapper
│   │   ├── config/               # Redis / MinIO / MyBatis-Plus / Security / 缓存
│   │   ├── filters/              # JwtAuthenticationFilter
│   │   └── listen/               # RedisKeyExpirationListener
│   ├── src/main/resources/application.yaml
│   └── pom.xml
├── A7vue3Project/                # Vue3 前端
│   ├── src/{api,components,layouts,modules,router,routers,views,utils,usePinia}
│   ├── vite.config.*  package.json
└── langchain_service/            # Python AI 服务（FastAPI + LangChain）
    ├── main.py                   # 全部 HTTP 接口
    ├── config.py                 # LLM / Embedding / RAG / 端口 配置
    ├── chains/                   # base / teacher_chains / student_chains
    ├── models/                   # prompts.py（提示词）+ schemas.py（请求模型）
    ├── rag/                      # document_loader / embeddings / vector_store / retriever
    ├── knowledge_base/courses/   # Markdown 课件（知识源）
    ├── chroma_db/                # 向量库持久化目录
    ├── requirements.txt
    └── .env.example
```

> **配置说明**：`application.yaml` 中的 MySQL 密码、JWT 密钥、MinIO AccessKey/SecretKey
> 已改为环境变量占位（`${MYSQL_PASSWORD}`、`${JWT_SECRET}`、`${MINIO_ACCESS_KEY}`、`${MINIO_SECRET_KEY}`），
> 本地开发可直接用默认值，部署时请通过环境变量覆盖。

---

## 六、快速开始

> 三步：起中间件 → 起 AI 服务 → 起后端/前端。完整步骤见 [`REPRODUCE.md`](REPRODUCE.md)。

```bash
# 1) 基础设施
#    MySQL: 建库 A7（用 application.yaml 里的连接串）
#    Redis : 默认 localhost:6379
#    MinIO : 默认 9000/9001
#    Ollama: 拉取 embedding 模型
ollama pull nomic-embed-text

# 2) AI 服务（:9084）
cd langchain_service
python -m venv .venv && .venv/Scripts/activate      # Windows
pip install -r requirements.txt
cp .env.example .env                                # 填 DEEPSEEK_API_KEY
python main.py

# 3) 后端（:9080）
cd ../A7SpringBoot && mvn spring-boot:run

# 4) 前端（:9070）
cd ../A7vue3Project && npm install && npm run dev
```

**默认端口**：前端 `9070` · 后端 `9080` · AI 服务 `9084`

---

## 七、配置说明

**`langchain_service/.env`**
```ini
DEEPSEEK_API_KEY=sk-xxxxxxxx
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-v4-pro
OLLAMA_BASE_URL=http://localhost:11434
EMBEDDING_MODEL=nomic-embed-text
```

**`A7SpringBoot/src/main/resources/application.yaml`**：MySQL（库 `A7`）、Redis、文件上传上限、
`SPRING_BOOT_URL`（AI 服务回调地址，默认 `http://localhost:9080`）等。

> 🔐 仓库已加入 `.gitignore` 忽略 `.env`，**不要提交任何 API Key**。

---

## 八、说明

- 本项目为教学/竞赛用途，默认使用云端 DeepSeek API + 本地 Ollama Embedding，成本可控。
- `chroma_db/` 内是演示用的向量库（含 JAVA、数据结构等课程），可以直接删掉重新构建。
- 若要接自建模型，只需改 `config.py` 里的 `DEEPSEEK_BASE_URL` / `DEEPSEEK_MODEL`（兼容 OpenAI 协议即可）。

## 九、License

MIT
