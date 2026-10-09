# 智语教育 复现教程（三服务本地跑通）

> 目标：在一台开发机上把 **MySQL/Redis/MinIO/Ollama/ChromaDB → AI 服务(:9084) → 后端(:9080) → 前端(:9070)** 全部跑起来，
> 并完成"教师建知识库 + 出题、学生练习 + 自动批改"最小闭环。

---

## 0. 复现清单

- [ ] 环境：Java 17、Maven 3.9+、Node 18+、Python 3.11、MySQL 8、Redis、MinIO、Ollama
- [ ] 申请 DeepSeek API Key
- [ ] 建 MySQL 库 `A7`；启动 Redis / MinIO；`ollama pull nomic-embed-text`
- [ ] `langchain_service`：装依赖 → 配 `.env` → `python main.py` → `/health` 返回 ok
- [ ] `A7SpringBoot`：改 `application.yaml` → `mvn spring-boot:run` → 登录接口可用
- [ ] `A7vue3Project`：`npm install` → `npm run dev` → 打开 :9070 能登录
- [ ] 教师：上传课件 → 组知识 → 出题；学生：练习 → 批改

---

## 1. 环境准备

| 组件 | 版本建议 | 用途 | 验证命令 |
| --- | --- | --- | --- |
| JDK | 17 | 后端 | `java -version` |
| Maven | 3.9+ | 后端构建 | `mvn -v` |
| Node.js | 18+（推荐 20） | 前端 | `node -v` |
| Python | 3.11 | AI 服务 | `python -V` |
| MySQL | 8.0 | 业务库 `A7` | `mysql -uroot -p` |
| Redis | 6+ | 缓存 / 过期监听 | `redis-cli ping` → PONG |
| MinIO | 最新 | 课件/附件对象存储 | 9001 控制台可登录 |
| Ollama | 最新 | **仅做 Embedding** | `ollama list` |
| DeepSeek API | — | 生成模型 | [platform.deepseek.com](https://platform.deepseek.com) 拿 Key |

---

## 2. 获取代码

```bash
git clone git@github.com:LokiThatYouKnow/zhiyujiaoyu.git
cd zhiyujiaoyu
```

> ⚠️ **当前这一版仓库只包含 `langchain_service/`**，`A7SpringBoot/` 与 `A7vue3Project/` 是空目录（代码未推送）。
> 要跑通完整闭环，需要先把后端与前端源码放进这两个目录（目录名保持一致即可）。

---

## 3. 启动中间件

### 3.1 MySQL
```sql
CREATE DATABASE A7 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```
> 后端 `application.yaml` 里 `jpa.hibernate.ddl-auto=update`，**表会自动建**；菜单/管理员等基础数据由
> `config/DataInitializer.java` 在启动时初始化。

### 3.2 Redis
```bash
redis-server                     # 或 Windows 下启动 redis 服务
redis-cli ping                   # 期望 PONG
```

### 3.3 MinIO
```bash
minio server D:\minio-data --console-address ":9001"
```
按 `application.yaml` 里的 `minio.*` 配置创建对应的 bucket（课件/附件上传需要）。

### 3.4 Ollama（Embedding）
```bash
ollama pull nomic-embed-text
ollama list                      # 确认模型存在
curl http://localhost:11434/api/tags
```

---

## 4. 启动 AI 服务（:9084）

```bash
cd langchain_service

# 建虚拟环境
python -m venv .venv
.venv\Scripts\activate            # Windows
# source .venv/bin/activate        # Linux / macOS

pip install -r requirements.txt

# 配置密钥
copy .env.example .env             # Windows（Linux: cp）
# 编辑 .env，填入 DEEPSEEK_API_KEY

python main.py                     # 监听 0.0.0.0:9084
```

**验证**：
```bash
curl http://localhost:9084/api/langchain/health
curl http://localhost:9084/api/langchain/models
curl http://localhost:9084/api/langchain/knowledge/collections
```
第一个返回健康状态，第三个应列出 `chroma_db/` 里已有的 collection（JAVA知识、数据结构与算法 等演示库）。

---

## 5. 启动后端（:9080）

```bash
cd ../A7SpringBoot
# 按需修改 src/main/resources/application.yaml：
#   spring.datasource.url / username / password   → 你的 MySQL
#   spring.data.redis.host / port                 → 你的 Redis
#   minio.*                                       → 你的 MinIO
#   （AI 服务回调地址在 langchain_service/config.py 里，默认 http://localhost:9080）

mvn clean package -DskipTests
mvn spring-boot:run
```
**验证**：访问登录接口（`/api/...`）或直接用前端登录页测试；日志里应能看到 JPA 建表与数据初始化。

> Windows 中文路径下若 `mvn` 报编码错误，加 `-Dfile.encoding=UTF-8`。

---

## 6. 启动前端（:9070）

```bash
cd ../A7vue3Project
npm install
npm run dev            # package.json 已固定 --port 9070 --strictPort
```
浏览器打开 `http://localhost:9070`。

> 前端请求后端的地址在 `src/api/`（Axios 实例）与 `vite.config.*` 的代理里配置；
> 若后端换了端口，这里要同步改。

---

## 7. 导入知识库（RAG 闭环）

1. 把课件放到 `langchain_service/knowledge_base/courses/`（支持 `.md`，演示库里有 JAVA / 数据结构等示例）
2. 导入方式二选一：
   - **走接口**：调用 `POST /api/langchain/teacher/addCourseKnowledge`（教师端"添加课程知识"就是这个接口）
   - **写脚本批量灌库**：`rag/document_loader.py`（切分：1000/200）+`rag/vector_store.py`（写 Chroma），
     两个模块就是为批量导入设计的，几行就能跑
3. 向量库按**课程名分 collection**，多课程天然隔离；`chroma_db/` 可整体删除后重建

---

## 8. 最小闭环验证

| 步骤 | 操作 | 期望 |
| --- | --- | --- |
| 1 | 教师端登录 → 上传课件 | 文件落到 MinIO，返回课件记录 |
| 2 | 教师端"知识组织" | 返回结构化知识点，`chroma_db` 新增 collection |
| 3 | 教师端"出题/练习设计" | 生成题目并入库，可在题库列表看到 |
| 4 | 学生端"自主练习" | 生成题目 → 作答 → 返回批改结果与解析 |
| 5 | 学生端"错题/学情" | 正确率、知识点掌握度更新，看板数字变化 |

命令行也可以直接压 AI 服务（不经过后端）：
```bash
curl -X POST http://localhost:9084/api/langchain/student/selfPractise ^
     -H "Content-Type: application/json" ^
     -d "{\"course_name\":\"JAVA知识\",\"knowledge_point\":\"集合框架\",\"count\":3}"
```
（字段以 `models/schemas.py` 为准，具体请求体会在后端 `LangChainClientService` 里组装。）

---

## 9. 常见问题

**Q1. `/health` 通但调用接口报 Embedding 错误**
Ollama 没起或模型没拉：`ollama pull nomic-embed-text`，并确认 `OLLAMA_BASE_URL` 可达。

**Q2. 调用报 401 / 余额不足**
`.env` 里 `DEEPSEEK_API_KEY` 没配或失效；也确认 `DEEPSEEK_MODEL` 值（默认 `deepseek-v4-pro`）在你的账号下可用。

**Q3. 检索结果为空 / 答非所问**
① 该课程的 collection 还没建（先导知识）；② `CHUNK_SIZE/OVERLAP` 与课件结构不匹配，可在 `config.py` 调；③ `MAX_RETRIEVED_DOCS`（默认 6）太小。

**Q4. Chroma 报维度不一致**
换过 Embedding 模型导致向量维度变化 → 删除 `chroma_db/` 下对应 collection 重新灌库（**不同 Embedding 模型不能混用**）。

**Q5. 后端启动报 Redis 连接失败**
Redis 未启动，或 `spring.data.redis` 配置与实例不一致；后端有 `RedisKeyExpirationListener`，Redis 是必需项。

**Q6. 前端登录 401 / 跨域**
后端 JWT 过滤器与前端 token 存储（`src/utils`）需一致；跨域在 `SecurityConfig` 或 vite 代理里处理。

**Q7. 上传失败**
检查 MinIO 是否启动、bucket 是否创建、`max-file-size`（默认 50MB）是否超限。

**Q8. 端口冲突**
前端 9070 / 后端 9080 / AI 9084。改端口时三处都要对：`package.json`、`application.yaml`、`config.py`（`PORT` 与 `SPRING_BOOT_URL`）。

---

## 10. 安全与提交约定

- `.env` 已在 `.gitignore` 中，**切勿提交真实 API Key**（历史提交里已清理过明文密钥，注意别再犯）。
- `chroma_db/`、`__pycache__/`、`node_modules/`、`target/` 均不应入库。
- 生产部署时：AI 服务与后端之间建议加内网鉴权，MinIO/MySQL 不要暴露公网。

---

## 11. 一键启动顺序（备忘）

```
MySQL → Redis → MinIO → Ollama
      → langchain_service (python main.py, :9084)
      → A7SpringBoot      (mvn spring-boot:run, :9080)
      → A7vue3Project     (npm run dev, :9070)
```
顺序很重要：**AI 服务先起**，后端启动时要能连通它；否则涉及大模型的接口会直接超时。
